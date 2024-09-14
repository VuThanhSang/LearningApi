package com.example.learning_api.dto.request.forum;

import com.example.learning_api.enums.ForumStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateForumCommentRequest {
    private String forumId;
    private String content;
    private String authorId;
    private String role;
    private ForumStatus status;
    private List<MultipartFile> sources;
    private String parentId;
}
