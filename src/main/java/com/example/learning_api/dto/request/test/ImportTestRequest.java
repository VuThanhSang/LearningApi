package com.example.learning_api.dto.request.test;

import com.example.learning_api.enums.ImportType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportTestRequest {
    @NotBlank
    private String testId;
    @NotBlank
    private ImportType type;
    private MultipartFile file;
    private String text;
}
