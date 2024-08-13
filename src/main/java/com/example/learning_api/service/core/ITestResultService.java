package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.SaveProgressRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.*;
import com.example.learning_api.entity.sql.database.StudentEntity;

import java.util.List;

public interface ITestResultService {
    StartTestResponse addTestResult(CreateTestResultRequest body);
    void updateTestResult(UpdateTestResultRequest body);
    void deleteTestResult(String studentId, String courseId);
    void saveProgress(SaveProgressRequest body);
    List<TestResultsForClassroomResponse> getTestResultsForClassroom(String classroomId);
    List<TestResultForStudentResponse> getTestResultsByStudentIdAndClassroomId(String studentId, String classroomId);
    OverviewResultResponse getOverviewOfTestResults(String testId);
    StatisticsResultResponse getStatisticsQuestionAndAnswerOfTest(String testId);
    List<StudentEntity> getStudentNotAttemptedTest(String testId);
    List<ScoreDistributionResponse> getScoreDistributionOfTest(String testId);


}
