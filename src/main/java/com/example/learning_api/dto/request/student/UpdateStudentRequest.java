package com.example.learning_api.dto.request.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStudentRequest {
    @NotBlank
    private String id;
    private String gradeLevel;
    private String gender;
    private String address;
    private String phone;
    private String schoolYear;
}
