package com.example.learning_api.dto.request.media;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateMediaRequest {
    @NotBlank
    private String id;
    private String description;
    private String name;
    private MultipartFile file;
}
