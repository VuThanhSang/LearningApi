package com.example.learning_api.dto.request.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateResourceRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private MultipartFile file;
}
