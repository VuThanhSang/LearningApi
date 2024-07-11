package com.example.learning_api.dto.request.discussion;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateDiscussionCommentRequest {
    private String id;
    private String content;
    private int upvote;
    private int downvote;
    private int replyCount;
    private String status;
    private MultipartFile source;

}
