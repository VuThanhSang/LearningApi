package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.test.StartTestResponse;

public interface ITestResultService {
    StartTestResponse addTestResult(CreateTestResultRequest body);
    void updateTestResult(UpdateTestResultRequest body);
    void deleteTestResult(String studentId, String courseId);
}
