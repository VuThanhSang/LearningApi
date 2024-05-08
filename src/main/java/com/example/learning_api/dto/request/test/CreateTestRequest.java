package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
public class CreateTestRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String createdBy;
    @NotBlank
    private int duration;
    private MultipartFile source;
    private Date createdAt;
    private Date updatedAt;
}
