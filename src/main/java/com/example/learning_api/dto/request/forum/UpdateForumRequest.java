package com.example.learning_api.dto.request.forum;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateForumRequest {
    private String id;
    private String title;
    private String content;
    private List<MultipartFile> sources;
    private String status;
}
