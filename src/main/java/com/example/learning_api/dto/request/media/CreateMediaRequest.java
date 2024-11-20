package com.example.learning_api.dto.request.media;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateMediaRequest {
    @NotBlank
    private String lessonId;
    @NotBlank
    private MultipartFile file;
    private String description;
    private String filePath;
    @NotBlank
    private String name;

}
