package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;

public interface ITestResultService {
    void addTestResult(CreateTestResultRequest body);
    void updateTestResult(UpdateTestResultRequest body);
    void deleteTestResult(String studentId, String courseId);
}
