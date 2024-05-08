package com.example.learning_api.dto.request.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateResourceRequest {
    @NotBlank
    private String lessonId;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private MultipartFile file;

}
