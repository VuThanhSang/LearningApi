package com.example.learning_api.dto.response.teacher;

import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

@Data
public class CreateTeacherResponse {
    private UserEntity user;
    private String id;
    private String userId;
    private String bio;
    private String qualifications;
}
