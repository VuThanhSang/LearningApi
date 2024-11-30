package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.response.test.GetTestDetailResponse;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IUserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.USER_AUTH_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
     private final IUserAuthService userAuthService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseAPI<UserEntity>> getTestDetail(@PathVariable String id) {
        try{
            UserEntity resData = userAuthService.getUserById(id);
            ResponseAPI<UserEntity> res = ResponseAPI.<UserEntity>builder()
                    .timestamp(new Date())
                    .message("Get test detail successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<UserEntity> res = ResponseAPI.<UserEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


}
