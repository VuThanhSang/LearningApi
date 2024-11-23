package com.example.learning_api.dto.request.forum;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateForumRequest {
    private String title;
    private String content;
    private String authorId;
    private List<MultipartFile> sources;
    private String status;
    private List<String> tags;
    private String role;
}
