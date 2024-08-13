package com.example.learning_api.dto.request.test_feedback;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateTestFeedbackRequest {
    private String id;
    private List<MultipartFile> sources;
    private String feedback;
}
