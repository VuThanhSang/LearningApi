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
}
