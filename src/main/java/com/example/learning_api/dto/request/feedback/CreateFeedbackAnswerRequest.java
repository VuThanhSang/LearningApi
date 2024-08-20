package com.example.learning_api.dto.request.feedback;

import lombok.Data;

@Data
public class CreateFeedbackAnswerRequest {
    private String feedbackId;
    private String answer;
    private String teacherId;
}
