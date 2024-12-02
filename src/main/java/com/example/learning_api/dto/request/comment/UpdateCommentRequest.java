package com.example.learning_api.dto.request.comment;

import com.example.learning_api.dto.common.SourceUploadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateCommentRequest {
    @NotBlank
    private String id;
    private String content;
    private String status;
    private List<MultipartFile> sources;

}
