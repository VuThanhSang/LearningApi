package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.common.MessageDto;
import com.example.learning_api.dto.common.TokenDto;
import com.example.learning_api.dto.common.UrlDto;
import com.example.learning_api.model.OauthProperties;

import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static com.example.learning_api.constant.RouterConstant.DEADLINE_BASE_PATH;
import static com.example.learning_api.constant.RouterConstant.USER_AUTH_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@Slf4j
public class DemoController {
    private final IAdminService adminService;
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ResponseAPI<String>> deleteDeadline(@PathVariable String fileId) {
        try{
            adminService.removeFile(fileId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("remove file successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }




}
