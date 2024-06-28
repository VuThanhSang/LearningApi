package com.example.learning_api.dto.request.deadline;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateDeadlineSubmissionsRequest {
    @NotBlank
    private String id;
    private String title;
    private MultipartFile file;
    private String submission;
    private String grade;
    private String feedback;
    private String status;
}
