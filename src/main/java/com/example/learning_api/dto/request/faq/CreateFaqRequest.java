package com.example.learning_api.dto.request.faq;

import com.example.learning_api.enums.FaqTargetType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFaqRequest {
    @NotBlank
    private String question;
    @NotBlank
    private String userId;
    @NotBlank
    private String targetId;
    @NotBlank
    private FaqTargetType type;
}
