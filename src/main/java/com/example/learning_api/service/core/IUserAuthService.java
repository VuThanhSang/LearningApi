package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.auth.ChangePasswordRequest;
import com.example.learning_api.dto.request.auth.LoginUserRequest;
import com.example.learning_api.dto.request.auth.RegisterUserRequest;
import com.example.learning_api.dto.response.auth.LoginResponse;
import com.example.learning_api.dto.response.auth.RefreshTokenResponse;
import com.example.learning_api.dto.response.auth.RegisterResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    LoginResponse loginUser(LoginUserRequest body);
    LoginResponse loginGoogleUser(OAuth2User oAuth2User);
    RefreshTokenResponse refreshToken(String refreshToken);
    void logout(String userId);
    void sendCodeToRegister(String email);

    void sendCodeForgotPassword(String email);

    void verifyCodeByEmail(String code, String email);

    @Transactional
    void changePasswordForgot(ChangePasswordRequest body);


}
