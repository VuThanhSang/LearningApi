package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.ChangePasswordRequest;
import com.example.learning_api.dto.request.LoginUserRequest;
import com.example.learning_api.dto.request.RegisterUserRequest;
import com.example.learning_api.dto.response.LoginResponse;
import com.example.learning_api.dto.response.RefreshTokenResponse;
import com.example.learning_api.dto.response.RegisterResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    LoginResponse loginUser(LoginUserRequest body);
    LoginResponse loginGoogleUser(OAuth2User oAuth2User);
    RefreshTokenResponse refreshToken(String refreshToken);

    void sendCodeToRegister(String email);

    void sendCodeForgotPassword(String email);

    void verifyCodeByEmail(String code, String email);

    @Transactional
    void changePasswordForgot(ChangePasswordRequest body);


}
