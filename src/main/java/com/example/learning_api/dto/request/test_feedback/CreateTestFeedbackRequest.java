package com.example.learning_api.dto.request.test_feedback;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateTestFeedbackRequest {
    private String testId;
    private String studentId;
    private List<MultipartFile> sources;
    private String feedback;
    private String title;
}
