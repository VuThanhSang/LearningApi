package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.enums.DiscussionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateDiscussionRequest {
    private String title;
    private String content;
    private String authorId;
    private MultipartFile source;
    private String status;
    private String role;
}
