package com.example.learning_api.dto.request.deadline;

import lombok.Data;

@Data
public class GradeSubmissionRequest {
    private String grade;
    private String feedback;
}
