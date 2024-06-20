package com.example.learning_api.dto.request.faculty;

import com.example.learning_api.enums.ImportType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportFacultyRequest {
    private String text;
    @NotBlank
    private ImportType type;
    private MultipartFile file;
}
