package com.example.learning_api.controller;


import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.constant.SuccessConstant;
import com.example.learning_api.dto.request.auth.*;
import com.example.learning_api.dto.response.auth.LoginResponse;
import com.example.learning_api.dto.response.auth.RefreshTokenResponse;
import com.example.learning_api.dto.response.auth.RegisterResponse;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
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

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
    private final JwtService jwtService;
    @Operation(summary = USER_AUTH_REGISTER_SUM, description = USER_AUTH_REGISTER_DESC)
    @PostMapping(POST_USER_AUTH_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<RegisterResponse>> registerUser(@RequestBody @Valid RegisterUserRequest body) {
        try{
            RegisterResponse resDate = userAuthService.registerUser(body);
            ResponseAPI<RegisterResponse> res = ResponseAPI.<RegisterResponse>builder()
                    .timestamp(new Date())
                    .message("Register successfully")
                    .data(resDate)
                    .build();
            HttpHeaders headers = CookieUtils.setRefreshTokenCookie(resDate.getRefreshToken(),604800L);
            return new ResponseEntity<>(res, headers, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<RegisterResponse> res = ResponseAPI.<RegisterResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }


    }

    @Operation(summary = USER_AUTH_REFRESH_SUM)
    @GetMapping(USER_AUTH_REFRESH_PATH)
    public ResponseEntity<ResponseAPI<RefreshTokenResponse>> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {

       try {
           if (refreshToken == null) {
               ResponseAPI<RefreshTokenResponse> res = ResponseAPI.<RefreshTokenResponse>builder()
                       .timestamp(new Date())
                       .message(ErrorConstant.UNAUTHORIZED)
                       .build();
               return new ResponseEntity<>(res, StatusCode.UNAUTHORIZED);
           }
           RefreshTokenResponse data = userAuthService.refreshToken(refreshToken);

           ResponseAPI<RefreshTokenResponse> res = ResponseAPI.<RefreshTokenResponse>builder()
                   .timestamp(new Date())
                   .data(data)
                   .message(SuccessConstant.GET)
                   .build();

           HttpHeaders headers = CookieUtils.setRefreshTokenCookie(data.getRefreshToken(), 604800L);
           return new ResponseEntity<>(res, headers, StatusCode.OK);
       }
         catch (Exception e){
              ResponseAPI<RefreshTokenResponse> res = ResponseAPI.<RefreshTokenResponse>builder()
                     .timestamp(new Date())
                     .message(e.getMessage())
                     .build();
              return new ResponseEntity<>(res, StatusCode.UNAUTHORIZED);
         }

    }

    @Operation(summary = USER_AUTH_LOGIN_SUM)
    @PostMapping(POST_USER_AUTH_LOGIN_SUB_PATH)
    public ResponseEntity<ResponseAPI<LoginResponse>> loginUser(@RequestBody @Valid LoginUserRequest body) {

        try {
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
        catch (Exception e){
            ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                    .timestamp(new Date())
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return  new ResponseEntity<>(res, StatusCode.UNAUTHORIZED);
        }


    }
    @Operation(summary = USER_AUTH_GOOGLE_LOGIN_SUM)
    @GetMapping(USER_AUTH_GOOGLE_LOGIN_PATH)
    public ResponseEntity<ResponseAPI<LoginResponse>> googleAuth(@AuthenticationPrincipal OAuth2User principal) {
        try{
            if (principal == null) {
                ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                        .timestamp(new Date())
                        .success(false)
                        .message(ErrorConstant.UNAUTHORIZED)
                        .build();
                return  new ResponseEntity<>(res, StatusCode.UNAUTHORIZED);
            }
            LoginResponse data = userAuthService.loginGoogleUser(principal);
            ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                    .timestamp(new Date())
                    .success(true)
                    .message(SuccessConstant.LOGIN)
                    .data(data)
                    .build();
            HttpHeaders headers = CookieUtils.setRefreshTokenCookie(data.getRefreshToken(), 604800L);
            return new ResponseEntity<>(res, headers, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                    .timestamp(new Date())
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return  new ResponseEntity<>(res, StatusCode.UNAUTHORIZED);
        }

    }

    @Operation(summary = USER_AUTH_LOGOUT_SUM)
    @GetMapping("/logout/{userId}")
    public ResponseEntity<ResponseAPI<?>> logoutUser(@CookieValue(name = "refreshToken", required = false) String refreshToken, @PathVariable String userId
                                                     ) {

        if (refreshToken == null) {
            throw new CustomException(ErrorConstant.NOT_FOUND);
        }
        userAuthService.logout(userId);
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.LOGOUT)
                .build();

        HttpHeaders headers = CookieUtils.setRefreshTokenCookie("", 0L);
        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }



    @Operation(summary = USER_AUTH_SEND_CODE_TO_EMAIL_TO_REGISTER_SUM)
    @PostMapping(POST_USER_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> sendCodeToRegister(@RequestBody @Valid SendCodeRequest body) {
        try {
            userAuthService.sendCodeToRegister(body.getEmail());
            ResponseAPI<?> res = ResponseAPI.builder()
                    .timestamp(new Date())
                    .message(SuccessConstant.SEND_CODE_TO_EMAIL)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }catch (Exception e){
            e.printStackTrace();

            return new ResponseEntity<>(ResponseAPI.builder()
                    .message(e.getMessage())
                    .timestamp(new Date())
                    .build(), StatusCode.BAD_REQUEST);
        }


    }

    @Operation(summary = USER_AUTH_SEND_CODE_TO_EMAIL_TO_GET_PWD_SUM)
    @PostMapping(POST_AUTH_SEND_CODE_FORGOT_PWD)
    public ResponseEntity<ResponseAPI<?>> sendCodeToGetPassword(@RequestBody @Valid SendCodeRequest body) {
        try {
            userAuthService.sendCodeForgotPassword(body.getEmail());
            ResponseAPI<?> res = ResponseAPI.builder()
                    .timestamp(new Date())
                    .message(SuccessConstant.SEND_CODE_TO_EMAIL)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }catch (Exception e){
            return new ResponseEntity<>(ResponseAPI.builder()
                    .message(e.getMessage())
                    .timestamp(new Date())
                    .build(), StatusCode.BAD_REQUEST);
        }


    }

    @Operation(summary = USER_AUTH_VERIFY_EMAIL_SUM)
    @PostMapping(POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> verifyCodeByEmail(@RequestBody @Valid VerifyEmailRequest body) {


        try {
            userAuthService.verifyCodeByEmail(body.getCode(), body.getEmail());
            ResponseAPI<?> res = ResponseAPI.builder()
                    .message(SuccessConstant.EMAIL_VERIFIED)
                    .timestamp(new Date())
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }catch (Exception e){
            return new ResponseEntity<>(ResponseAPI.builder()
                    .message(e.getMessage())
                    .timestamp(new Date())
                    .build(), StatusCode.BAD_REQUEST);
        }
    }



    @Operation(summary = USER_AUTH_CHANGE_PASSWORD_SUM)
    @PatchMapping(PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> changePasswordForgot(@RequestBody @Valid ChangePasswordRequest body) {
        try {
            userAuthService.changePasswordForgot(body);

            ResponseAPI<?> res = ResponseAPI.builder()
                    .message(SuccessConstant.UPDATED)
                    .timestamp(new Date())
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(ResponseAPI.builder()
                    .message(e.getMessage())
                    .timestamp(new Date())
                    .build(), StatusCode.BAD_REQUEST);
        }

    }

}
