package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTestRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private int duration;
}
