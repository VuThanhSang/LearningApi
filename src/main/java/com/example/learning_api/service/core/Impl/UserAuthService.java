package com.example.learning_api.service.core.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.kafka.CodeEmailDto;
import com.example.learning_api.dto.request.ChangePasswordRequest;
import com.example.learning_api.dto.request.LoginUserRequest;
import com.example.learning_api.dto.request.RegisterUserRequest;
import com.example.learning_api.dto.response.LoginResponse;
import com.example.learning_api.dto.response.RefreshTokenResponse;
import com.example.learning_api.dto.response.RegisterResponse;
import com.example.learning_api.entity.sql.database.ConfirmationEntity;
import com.example.learning_api.entity.sql.database.TokenEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.ConfirmationCodeStatus;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.kafka.publisher.MailerKafkaPublisher;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.ConfirmationRepository;
import com.example.learning_api.repository.database.TokenRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IUserAuthService;
import com.example.learning_api.utils.GeneratorUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.ErrorConstant.EXISTED_DATA;
import static com.example.learning_api.constant.ErrorConstant.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService implements IUserAuthService {

    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final ConfirmationRepository confirmationRepository;
    private final MailerKafkaPublisher mailerKafkaPublisher;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterUserRequest body) {
        UserEntity userEntity = modelMapperService.mapClass(body, UserEntity.class);
        if(userRepository.findByEmail(userEntity.getEmail()).orElse(null) != null) {
            throw new CustomException(EXISTED_DATA, "Email already registered");
        }

        RegisterResponse resData = new RegisterResponse();
        modelMapperService.map(userEntity, resData);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setAuthType("normal");
        userEntity.setCreatedAt(new Date());
        userEntity.setUpdatedAt(new Date());
        userEntity = userRepository.save(userEntity);
        var accessToken = jwtService.issueAccessToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole());
        var refreshToken = jwtService.issueRefreshToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole());
//        userTokenRedisService.createNewUserRefreshToken(refreshToken, userEntity.getId());
        resData.setAccessToken(accessToken);
        resData.setRefreshToken(refreshToken);
        resData.setUserId(userEntity.getId());
        return resData;
    }

    @Override
    public LoginResponse loginUser(LoginUserRequest body) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        body.getEmail(),
                        body.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(body.getEmail()).orElseThrow();
        String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());
        revokeAllTokenByUser(user);
        saveUserToken(refreshToken, user);
        return LoginResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public LoginResponse loginGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setFullname(oAuth2User.getAttribute("name"));
            user.setRole(RoleEnum.USER);
            user.setAuthType("google");
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            user = userRepository.save(user);
        }
        String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());

        return LoginResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        DecodedJWT decodedJWT = jwtService.decodeRefreshToken(refreshToken);
        String userId = decodedJWT.getSubject();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void revokeAllTokenByUser(UserEntity user) {
        List<TokenEntity> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }
    private void saveUserToken(String jwt, UserEntity user) {
        TokenEntity token = tokenRepository.findByToken(user.getId()).orElse(null);
        if (token != null) {
            // Update existing token
            token.setToken(jwt);
            token.setLoggedOut(false);
        } else {
            // Create new token
            token = new TokenEntity();
            token.setUser(user);
        }
        tokenRepository.save(token);
    }


    public void createOrUpdateConfirmationInfo(String email, String code) {
        ConfirmationEntity oldConfirmation = confirmationRepository.findByEmail(email).orElse(null);
        Date currentDate = new Date();
        Instant instant = currentDate.toInstant();
        Instant newInstant = instant.plus(Duration.of(3, ChronoUnit.MINUTES));
        Date newDate = Date.from(newInstant);
        if (oldConfirmation == null) {
            ConfirmationEntity confirmation = ConfirmationEntity.builder()
                    .email(email)
                    .code(code)
                    .status(ConfirmationCodeStatus.UNUSED)
                    .expireAt(newDate)
                    .build();
            confirmationRepository.save(confirmation);
        } else {
            oldConfirmation.setExpireAt(newDate);
            oldConfirmation.setCode(code);
            confirmationRepository.save(oldConfirmation);
        }
    }


    @Override
    public void sendCodeToRegister(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new CustomException(EXISTED_DATA, "Email is already registered");
        }
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        mailerKafkaPublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }
    @Override
    public void sendCodeToGetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + email));
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        mailerKafkaPublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }
    @Override
    public void verifyCodeByEmail(String code, String email) {
        ConfirmationEntity confirmationCollection = confirmationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Confirmation data with email " + email));
        Date currentTime = new Date();

        if (code.equals(confirmationCollection.getCode()) && currentTime.before(confirmationCollection.getExpireAt())) {
            confirmationCollection.setStatus(ConfirmationCodeStatus.USED);
            confirmationRepository.save(confirmationCollection);
            return;
        }

        throw new CustomException(UNAUTHORIZED, "Code is not valid");
    }

    @Transactional
    @Override
    public void changePasswordForgot(ChangePasswordRequest body) {
        UserEntity user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + body.getEmail()));

        ConfirmationEntity confirmation = confirmationRepository.findByEmailAndCode(body.getEmail(), body.getCode())
                .orElseThrow(() -> new CustomException(UNAUTHORIZED, "Email has not been verified"));
        if (confirmation.getStatus() != ConfirmationCodeStatus.USED) {
            throw new CustomException(UNAUTHORIZED, "Email has not been verified");
        }
        confirmationRepository.delete(confirmation);

        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }
}
