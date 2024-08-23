package com.example.learning_api.dto.request.feedback;

import com.example.learning_api.enums.FeedbackFormType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateFeedbackRequest {
    private String formId;
    private FeedbackFormType formType;
    private String studentId;
    private List<MultipartFile> files;
    private String feedback;
    private String title;
}
