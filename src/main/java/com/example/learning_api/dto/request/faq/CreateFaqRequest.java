package com.example.learning_api.dto.request.faq;

import com.example.learning_api.dto.common.SourceUploadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateFaqRequest {
    @NotBlank
    private String question;
    @NotBlank
    private String userId;
    @NotBlank
    private String classId;
    @NotBlank
    private String status;
    private List<MultipartFile> sources;
}
