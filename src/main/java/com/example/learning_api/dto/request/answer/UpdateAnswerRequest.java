package com.example.learning_api.dto.request.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateAnswerRequest {
    @NotBlank
    private String id;
    private String content;
    private Boolean isCorrect;
    private MultipartFile source;
    private Integer index;
}
