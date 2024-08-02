package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateExitLogRequest;
import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.SaveProgressRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.StartTestResponse;
import com.example.learning_api.entity.sql.database.StudentTestExitLogEntity;
import com.example.learning_api.repository.database.StudentTestExitLogRepository;

import java.util.List;

public interface ITestResultService {
    StartTestResponse addTestResult(CreateTestResultRequest body);
    void updateTestResult(UpdateTestResultRequest body);
    void deleteTestResult(String studentId, String courseId);
    void saveProgress(SaveProgressRequest body);
    void exitTestLog(CreateExitLogRequest body);
    List<StudentTestExitLogEntity> getTestResult(String studentId, String TestResultId);
}
