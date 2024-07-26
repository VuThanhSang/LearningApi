package com.example.learning_api.dto.request.deadline;

import com.example.learning_api.enums.DeadlineSubmissionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateDeadlineSubmissionsRequest {
    private String title;
    private String deadlineId;
    private String studentId;
    private MultipartFile file;
    private String submission;
    private DeadlineSubmissionStatus status;
}
