package com.example.learning_api.dto.request.user;

import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.UserStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateUserRequest {
    private String id;
    private String email;
    private String password;
    private String fullname;
    private RoleEnum role;
    private MultipartFile source;
    private UserStatus status;
    private String avatar;
}
