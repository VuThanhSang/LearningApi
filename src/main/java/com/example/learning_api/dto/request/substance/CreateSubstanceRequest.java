package com.example.learning_api.dto.request.substance;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSubstanceRequest {
    @NotBlank
    private String lessonId;
    private String name;
    private String content;
    private String status;
}
