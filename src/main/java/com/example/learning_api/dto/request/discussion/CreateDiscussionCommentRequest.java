package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.enums.DiscussionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateDiscussionCommentRequest {
    private String discussionId;
    private String content;
    private String authorId;
    private String role;
    private DiscussionStatus status;
    private MultipartFile source;
}
