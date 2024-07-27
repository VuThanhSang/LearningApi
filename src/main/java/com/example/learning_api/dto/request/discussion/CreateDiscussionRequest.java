package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.enums.DiscussionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateDiscussionRequest {
    private String title;
    private String content;
    private String authorId;
    private List<SourceUploadDto> sources;
    private String status;
    private List<String> tags;
    private String role;
}
