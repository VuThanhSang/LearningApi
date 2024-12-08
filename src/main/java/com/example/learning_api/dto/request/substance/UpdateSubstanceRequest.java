package com.example.learning_api.dto.request.substance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSubstanceRequest {
    @NotBlank
    private String id;
    private String content;
    private String name;
    private String status;
}
