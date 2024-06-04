package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTestResultRequest {
    @NotBlank
    private String id;

    private int grade;
    private boolean isPassed;

}
