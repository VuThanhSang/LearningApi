package com.example.learning_api.dto.request.student;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStudentRequest {
    @NotBlank
    private String userId;
    private String gradeLevel;
    private String gender;
    private String address;
    private String phone;
    private String academicYearId;
    private String majorId;

}
