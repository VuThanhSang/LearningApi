package com.example.learning_api.dto.request.forum;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateForumCommentRequest {
    private String id;
    private String content;
    private int replyCount;
    private String status;
    private List<MultipartFile> sources;

}
