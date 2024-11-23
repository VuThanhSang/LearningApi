package com.example.learning_api.dto.common;

import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.StudentStatus;
import lombok.Data;

import java.util.List;

@Data
public class StudentNotInClassDto {
    private String id;
    private String userId;
    private String gradeLevel;
    private UserEntity user;
    private String studentCode;
    private String gender;
    private String address;
    private String dateOfBirth;
    private String phone;
    private String schoolYear;
    private String academicYearId;
    private String majorId;
    private StudentStatus status;
    private String createdAt;
    private String updatedAt;
    private List<StudentEnrollmentsEntity> enrollment;
}
