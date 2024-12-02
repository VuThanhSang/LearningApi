package com.example.learning_api.dto.request.comment;


import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.enums.FaqStatus;
import com.example.learning_api.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateCommentRequest {
    @NotBlank
    private String faqId;
    @NotBlank
    private String userId;
    @NotBlank
    private FaqStatus status;
    @NotBlank
    private RoleEnum role;
    private String content;
    private String parentId;
    private List<MultipartFile> sources;

}
