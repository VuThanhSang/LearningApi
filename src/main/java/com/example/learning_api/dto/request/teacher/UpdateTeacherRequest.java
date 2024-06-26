package com.example.learning_api.dto.request.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTeacherRequest {
    @NotBlank
    private String id;
    private String bio;
    private String qualifications;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String experience;
    private String status;
}
