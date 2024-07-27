package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.enums.DiscussionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateDiscussionCommentRequest {
    private String discussionId;
    private String content;
    private String authorId;
    private String role;
    private DiscussionStatus status;
    private List<SourceUploadDto> sources;
    private String parentId;
}
