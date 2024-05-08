package com.example.learning_api.dto.request.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAnswerRequest {
    @NotBlank
    private String id;
    private String content;
    private boolean isCorrect;
    private String source;
}
