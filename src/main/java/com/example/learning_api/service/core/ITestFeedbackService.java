package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;

public interface ITestFeedbackService {
    void createTestFeedback(CreateTestFeedbackRequest body);
    void updateTestFeedback(UpdateTestFeedbackRequest body);
    void deleteTestFeedback(String testFeedbackId);
    void createTestFeedbackAnswer(CreateTestFeedbackAnswerRequest body);
    void updateTestFeedbackAnswer(String testFeedbackAnswerId, String answer);
    void deleteTestFeedbackAnswer(String testFeedbackAnswerId);
}
