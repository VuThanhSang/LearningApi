package com.example.learning_api.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateQuestionRequest {
    @NotBlank
    private String id;
    private String content;
    private String testId;
    private String description;
    private MultipartFile source;
    private String type;
}
