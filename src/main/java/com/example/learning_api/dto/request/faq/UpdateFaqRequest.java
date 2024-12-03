package com.example.learning_api.dto.request.faq;

import com.example.learning_api.dto.common.SourceUploadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateFaqRequest {
    @NotBlank
    private String id;
    private String question;
    private List<MultipartFile> sources;
    private String status;
    private String subject;

}
