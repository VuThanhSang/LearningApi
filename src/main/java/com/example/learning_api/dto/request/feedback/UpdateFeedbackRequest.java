package com.example.learning_api.dto.request.feedback;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateFeedbackRequest {
    private String id;
    private List<MultipartFile> sources;
    private String feedback;
    private String title;
}
