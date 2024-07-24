package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.test.StartTestResponse;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TestRepository;
import com.example.learning_api.repository.database.TestResultRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestResultService implements ITestResultService {
    private final TestResultRepository testResultRepository;
    private final StudentRepository studentRepository;
    private final ModelMapperService modelMapperService;
    private final TestRepository testRepository;


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
            if (count >= testEntity.getAttemptLimit()) {
                throw new IllegalArgumentException("You have reached the limit of attempts");
            }
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
}
