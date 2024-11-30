package com.example.learning_api.dto.request.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class CreateAnswerRequest {
    @NotBlank
    private String questionId;
    @NotBlank
    private String content;
    private boolean isCorrect;
    private MultipartFile source;
    private String answerText;
}
