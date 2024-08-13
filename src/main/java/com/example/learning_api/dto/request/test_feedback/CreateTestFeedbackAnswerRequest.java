package com.example.learning_api.dto.request.test_feedback;

import lombok.Data;

@Data
public class CreateTestFeedbackAnswerRequest {
    private String testFeedbackId;
    private String answer;
}
