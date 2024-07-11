package com.example.learning_api.dto.request.discussion;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateDiscussionRequest {
    private String id;
    private String title;
    private String content;
    private MultipartFile source;
    private String status;
}
