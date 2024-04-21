package com.example.learning_api.controller;


import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.constant.SuccessConstant;
import com.example.learning_api.dto.request.LoginUserRequest;
import com.example.learning_api.dto.request.RegisterUserRequest;
import com.example.learning_api.dto.response.LoginResponse;
import com.example.learning_api.dto.response.RegisterResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IUserAuthService;
import com.example.learning_api.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.example.learning_api.constant.SwaggerConstant.*;
import static com.example.learning_api.constant.RouterConstant.*;

@Tag(name = USER_AUTH_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(USER_AUTH_BASE_PATH)
@Slf4j
public class UserAuthController {
    private final IUserAuthService userAuthService;
    private static final Logger logger = LoggerFactory.getLogger(UserAuthController.class);
    private final ClientRegistrationRepository clientRegistrationRepository;
    @Operation(summary = USER_AUTH_REGISTER_SUM, description = USER_AUTH_REGISTER_DESC)
    @PostMapping(POST_USER_AUTH_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<RegisterResponse>> registerUser(@RequestBody @Valid RegisterUserRequest body) {
        RegisterResponse resDate = userAuthService.registerUser(body);
        ResponseAPI<RegisterResponse> res = ResponseAPI.<RegisterResponse>builder()
                .timestamp(new Date())
                .message("Register successfully")
                .data(resDate)
                .build();
        HttpHeaders headers = CookieUtils.setRefreshTokenCookie(resDate.getRefreshToken(),604800L);
        return new ResponseEntity<>(res, headers, StatusCode.CREATED);
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @Operation(summary = USER_AUTH_LOGIN_SUM)
    @PostMapping(POST_USER_AUTH_LOGIN_SUB_PATH)
    public ResponseEntity<ResponseAPI<LoginResponse>> loginUser(@RequestBody @Valid LoginUserRequest body) {
        LoginResponse data = userAuthService.loginUser(body);
        ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.LOGIN)
                .data(data)
                .build();

        HttpHeaders headers = CookieUtils.setRefreshTokenCookie(data.getRefreshToken(), 604800L);
        return new ResponseEntity<>(res, headers, StatusCode.OK);

    }

}
