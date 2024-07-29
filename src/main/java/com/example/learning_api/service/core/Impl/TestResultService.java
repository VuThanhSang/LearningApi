package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.SaveProgressRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.StartTestResponse;
import com.example.learning_api.entity.sql.database.StudentAnswersEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import com.example.learning_api.enums.TestState;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestResultService implements ITestResultService {
    private final TestResultRepository testResultRepository;
    private final StudentRepository studentRepository;
    private final ModelMapperService modelMapperService;
    private final TestRepository testRepository;
    private final StudentAnswersRepository studentAnswerRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Override
    public StartTestResponse addTestResult(CreateTestResultRequest body) {
        try{
            if (body.getTestId()==null || body.getStudentId()==null ) {
                throw new IllegalArgumentException("Test id, student id  must be provided");
            }
            if (testRepository.existsById(body.getTestId()) == false) {
                throw new IllegalArgumentException("Test does not exist");
            }
            if (studentRepository.existsById(body.getStudentId()) == false) {
                throw new IllegalArgumentException("Student does not exist");
            }

            TestResultEntity testResultEntity = modelMapperService.mapClass(body, TestResultEntity.class);
            TestEntity testEntity = testRepository.findById(body.getTestId()).orElseThrow(() -> new IllegalArgumentException("Test does not exist"));
            int count = testResultRepository.countByStudentIdAndTestId(body.getStudentId(), body.getTestId());
            if (testEntity.getAttemptLimit()==null){
                testEntity.setAttemptLimit(1);
            }

            TestResultEntity ongoingTest = testResultRepository.findFirstByStudentIdAndTestIdAndStateOrderByAttendedAtDesc(body.getStudentId(),body.getTestId(), TestState.ONGOING.name());
            if (ongoingTest != null){
                if (System.currentTimeMillis() - Long.parseLong(ongoingTest.getAttendedAt()) < testEntity.getDuration()  * 1000) {
                    throw new IllegalArgumentException("You have an ongoing test");
                }
            }
            if (count >= testEntity.getAttemptLimit()) {
                throw new IllegalArgumentException("You have reached the limit of attempts");
            }
            testResultEntity.setState(TestState.ONGOING);
            testResultEntity.setAttendedAt(String.valueOf(System.currentTimeMillis()));
            testResultEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            testResultEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            testResultRepository.save(testResultEntity);

            StartTestResponse startTestResponse = new StartTestResponse();
            startTestResponse.setStudentId(body.getStudentId());
            startTestResponse.setTestId(body.getTestId());
            startTestResponse.setTestResultId(testResultEntity.getId());
            startTestResponse.setAttendedAt(testResultEntity.getAttendedAt());
            return startTestResponse;

        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void updateTestResult(UpdateTestResultRequest body) {
        try{
            if (body.getGrade() < 0 || body.getGrade() > 100) {
                throw new IllegalArgumentException("Grade must be between 0 and 100");
            }
            TestResultEntity testResultEntity = testResultRepository.findById(body.getId()).orElseThrow(() -> new IllegalArgumentException("Test result does not exist"));
            if (body.getGrade() != 0 && body.getGrade() != testResultEntity.getGrade()) {
                testResultEntity.setGrade(body.getGrade());
            }
            if (body.isPassed() != testResultEntity.isPassed()) {
                testResultEntity.setPassed(body.isPassed());
            }

            testResultRepository.save(testResultEntity);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void deleteTestResult(String studentId, String courseId) {

        try{
//            TestResultEntity testResultEntity = testResultRepository.findByStudentIdAndTestId(studentId, courseId);
//            testResultRepository.delete(testResultEntity);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void saveProgress(SaveProgressRequest body) {
        try{
            TestResultEntity testResultEntity = testResultRepository.findById(body.getTestResultId()).orElseThrow(() -> new IllegalArgumentException("Test result does not exist"));
            if (testResultEntity.getState() == TestState.FINISHED) {
                throw new IllegalArgumentException("Test is already finished");
            }
            studentAnswerRepository.deleteByTestResultId(body.getTestResultId());
            for (SaveProgressRequest.QuestionAndAnswer questionAndAnswer : body.getQuestionAndAnswers()) {
                for (String answerId : questionAndAnswer.getAnswers()) {
                    StudentAnswersEntity studentAnswersEntity = new StudentAnswersEntity();
                    studentAnswersEntity.setAnswerId(answerId);
                    studentAnswersEntity.setQuestionId(questionAndAnswer.getQuestionId());
                    studentAnswersEntity.setTestResultId(body.getTestResultId());
                    studentAnswersEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    studentAnswersEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    studentAnswersEntity.setStudentId(testResultEntity.getStudentId());
                    studentAnswerRepository.save(studentAnswersEntity);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }




}
