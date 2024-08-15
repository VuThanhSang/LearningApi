package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;
import com.example.learning_api.dto.response.test_feedback.GetTestFeedBacksResponse;
import com.example.learning_api.entity.sql.database.TestFeedbackAnswerEntity;
import com.example.learning_api.entity.sql.database.TestFeedbackEntity;

import java.util.List;

public interface ITestFeedbackService {
    void createTestFeedback(CreateTestFeedbackRequest body);
    void updateTestFeedback(UpdateTestFeedbackRequest body);
    void deleteTestFeedback(String testFeedbackId);
    void createTestFeedbackAnswer(CreateTestFeedbackAnswerRequest body);
    void updateTestFeedbackAnswer(String testFeedbackAnswerId, String answer);
    void deleteTestFeedbackAnswer(String testFeedbackAnswerId);
    TestFeedbackEntity getTestFeedbackById(String testFeedbackId);
    List<TestFeedbackEntity> getTestFeedbacksByStudentIdAndTestId(String studentId, String testId);
    GetTestFeedBacksResponse getTestFeedbacksByTestId(String testId, String sort, int page, int size);
    List<TestFeedbackAnswerEntity> getTestFeedbackAnswersByFeedbackId(String testFeedbackId);

}
