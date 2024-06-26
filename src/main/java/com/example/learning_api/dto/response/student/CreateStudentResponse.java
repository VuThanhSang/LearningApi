package com.example.learning_api.dto.response.student;


import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

@Data
public class CreateStudentResponse {
    private String id;
    private String userId;
    private String gradeLevel;
    private String gender;
    private String address;
    private String phone;
    private String academicYearId;
    private String dateOfBirth;
    private String majorId;
    private UserEntity user;
}
