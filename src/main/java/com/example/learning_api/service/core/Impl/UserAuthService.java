package com.example.learning_api.service.core.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.auth.*;
import com.example.learning_api.dto.response.auth.*;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.*;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.*;
import com.example.learning_api.service.core.IUserAuthService;
import com.example.learning_api.utils.GeneratorUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService implements IUserAuthService {

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final ConfirmationRepository confirmationRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterUserRequest body) {
        if (userRepository.findByEmail(body.getEmail()).isPresent()) {
            throw new CustomException("Email already registered");
        }

        UserEntity userEntity = createUserEntity(body);
        userEntity = userRepository.save(userEntity);

        String accessToken = jwtService.issueAccessToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole(), null);
        String refreshToken = jwtService.issueRefreshToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole(), null);

        return buildRegisterResponse(userEntity, accessToken, refreshToken);
    }

    @Override
    public LoginResponse loginUser(LoginUserRequest body) {
        UserEntity user = authenticateUser(body.getEmail(), body.getPassword());
        String userRoleId = getUserRoleId(user);
        String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);
        String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);

        return buildLoginResponse(user, jwt, refreshToken);
    }

    @Override
    public LoginResponse loginGoogleUser(OAuth2User oAuth2User) {
        UserEntity user = getOrCreateGoogleUser(oAuth2User);
        String userRoleId = getUserRoleId(user);
        String jwt = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);
        String refreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);

        return buildLoginResponse(user, jwt, refreshToken);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        DecodedJWT decodedJWT = jwtService.decodeRefreshToken(refreshToken);
        String userId = decodedJWT.getSubject();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
        String userRoleId = getUserRoleId(user);

        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), user.getRole(), userRoleId);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    @Override
    public void logout(String userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
        revokeAllTokenByUser(user);
    }

    @Override
    @Async
    public void sendCodeToRegister(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User not found"));
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new CustomException("User already Active");
        }

        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        sendEmailWithCode(email, code, "Active User Successfully");
    }

    @Override
    public void sendCodeForgotPassword(String email) {
        userRepository.findByEmailAndAuthType(email, "normal")
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "User with email " + email));

        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        sendEmailWithCode(email, code, "Get Password Code Learning App");
    }

    @Override
    public void verifyCodeByEmail(String code, String email) {
        ConfirmationEntity confirmation = confirmationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "Confirmation data with email " + email));
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + "User with email " + email));

        validateConfirmationCode(confirmation, code);

        confirmation.setStatus(ConfirmationCodeStatus.USED);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        confirmationRepository.save(confirmation);
    }

    @Transactional
    @Override
    public void changePasswordForgot(ChangePasswordRequest body) {
        UserEntity user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + body.getEmail()));

        ConfirmationEntity confirmation = confirmationRepository.findByEmailAndCode(body.getEmail(), body.getCode())
                .orElseThrow(() -> new CustomException(ErrorConstant.UNAUTHORIZED, "Email has not been verified"));

        if (confirmation.getStatus() != ConfirmationCodeStatus.USED) {
            throw new CustomException(ErrorConstant.UNAUTHORIZED, "Email has not been verified");
        }

        confirmationRepository.delete(confirmation);
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }

    private UserEntity authenticateUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("Account not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Password is incorrect");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return user;
    }

    private LoginResponse buildLoginResponse(UserEntity user, String jwt, String refreshToken) {
        LoginResponse.LoginResponseBuilder responseBuilder = LoginResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .status(user.getStatus() != null ? user.getStatus().toString() : UserStatus.INACTIVE.toString());

        if (user.getRole() == RoleEnum.TEACHER) {
            TeacherEntity teacher = teacherRepository.findByUserId(user.getId());
            responseBuilder.teacher(teacher);
        } else if (user.getRole() == RoleEnum.USER) {
            StudentEntity student = studentRepository.findByUserId(user.getId());
            responseBuilder.student(student);
        }

        return responseBuilder.build();
    }

    private UserEntity createUserEntity(RegisterUserRequest body) {
        UserEntity userEntity = modelMapperService.mapClass(body, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setAuthType("normal");
        userEntity.setCreatedAt(new Date());
        userEntity.setUpdatedAt(new Date());
        userEntity.setStatus(UserStatus.INACTIVE);
        return userEntity;
    }

    private RegisterResponse buildRegisterResponse(UserEntity userEntity, String accessToken, String refreshToken) {
        RegisterResponse resData = new RegisterResponse();
        modelMapperService.map(userEntity, resData);
        resData.setAccessToken(accessToken);
        resData.setRefreshToken(refreshToken);
        resData.setUserId(userEntity.getId());
        return resData;
    }

    private UserEntity getOrCreateGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        return userRepository.findByEmailAndAuthType(email, "google")
                .orElseGet(() -> createGoogleUser(oAuth2User));
    }

    private UserEntity createGoogleUser(OAuth2User oAuth2User) {
        UserEntity user = new UserEntity();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setFullname(oAuth2User.getAttribute("name"));
        user.setRole(RoleEnum.USER);
        user.setAuthType("google");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    private String getUserRoleId(UserEntity user) {
        if (user.getRole() == RoleEnum.TEACHER) {
            TeacherEntity teacher = teacherRepository.findByUserId(user.getId());
            return teacher != null ? teacher.getId() : null;
        } else {
            StudentEntity student = studentRepository.findByUserId(user.getId());
            return student != null ? student.getId() : null;
        }
    }

    private void revokeAllTokenByUser(UserEntity user) {
        List<TokenEntity> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(t -> t.setLoggedOut(true));
            tokenRepository.saveAll(validTokens);
        }
    }

    private void createOrUpdateConfirmationInfo(String email, String code) {
        ConfirmationEntity confirmation = confirmationRepository.findByEmail(email).orElse(new ConfirmationEntity());
        Date expirationDate = Date.from(Instant.now().plus(Duration.of(3, ChronoUnit.MINUTES)));

        confirmation.setEmail(email);
        confirmation.setCode(code);
        confirmation.setStatus(ConfirmationCodeStatus.UNUSED);
        confirmation.setExpireAt(expirationDate);

        confirmationRepository.save(confirmation);
    }

    private void sendEmailWithCode(String toMail, String body, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("body", body);
            context.setVariable("toMail", toMail);
            String htmlContent = templateEngine.process("email-template", context);

            mimeMessageHelper.setFrom(mailFrom);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject(subject);

            javaMailSender.send(mimeMessage);
            log.info("Send email to {} successfully", toMail);
        } catch (MessagingException e) {
            throw new CustomException("Error while sending email");
        }
    }

    private void validateConfirmationCode(ConfirmationEntity confirmation, String code) {
        if (!code.equals(confirmation.getCode())) {
            throw new CustomException("Code is incorrect");
        }

        if (new Date().after(confirmation.getExpireAt())) {
            throw new CustomException("Code has expired");
        }
    }
}