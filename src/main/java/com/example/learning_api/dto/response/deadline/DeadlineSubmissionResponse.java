package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import lombok.Data;

import java.util.List;

@Data
public class DeadlineSubmissionResponse {
    private String id;
    private String title;
    private String deadlineId;
    private String studentId;
    private String submission;
    private String grade;
    private String feedback;
    private DeadlineSubmissionStatus status;
    private List<FileEntity> files;
    private String createdAt;
    private String updatedAt;
}
