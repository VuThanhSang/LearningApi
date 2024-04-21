package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.LoginUserRequest;
import com.example.learning_api.dto.request.RegisterUserRequest;
import com.example.learning_api.dto.response.LoginResponse;
import com.example.learning_api.dto.response.RegisterResponse;
import com.example.learning_api.entity.sql.database.TokenEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.TokenRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.secutiry.UserPrincipal;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IUserAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.learning_api.constant.ErrorConstant.EXISTED_DATA;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService implements IUserAuthService {

    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
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
                .userId(user.getId())
                .build();
//        UserPrincipal userPrincipal = UserPrincipal.builder()
//                .username(body.getEmail())
//                .password(body.getPassword())
//                .build();
//        Authentication userCredential = new UserUsernamePasswordAuthenticationToken(userPrincipal);
//        var authentication  = authenticationManager.authenticate(userCredential);
//        var principalAuthenticated = (UserPrincipal) authentication.getPrincipal();
//
//        UserEntity user = userRepository.findByEmail(userPrincipal.getUsername()).orElse(null);
//        SecurityContextHolder.getContext().setAuthentication(userCredential);
//        var roles = authentication.getAuthorities()
//                .stream().map(GrantedAuthority::getAuthority)
//                .toList();
//        String userId = principalAuthenticated.getUserId();
//        String username = principalAuthenticated.getUsername();
//        var accessToken = jwtService.issueAccessToken(userId, username, roles);
//        var refreshToken = jwtService.issueRefreshToken(userId, username, roles);
////        userTokenRepository
//        return LoginResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .userId(userId)
//                .build();
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
}
