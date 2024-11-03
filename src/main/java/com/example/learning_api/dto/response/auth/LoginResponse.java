package com.example.learning_api.dto.response.auth;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    @JsonIgnore
    private final String refreshToken;
    private final UserEntity user;
    private final String status;
    private final String role;
}
