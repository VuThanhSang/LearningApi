package com.example.learning_api.dto.request.faq;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFaqRequest {
    @NotBlank
    private String id;
    private String question;
}
