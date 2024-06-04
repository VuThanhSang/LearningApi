package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.StudentRepository;
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
    private final CourseRepository courseRepository;
    private final ModelMapperService modelMapperService;


    @Override
    public void addTestResult(CreateTestResultRequest body) {
        try{
            if (body.getGrade() < 0 || body.getGrade() > 100) {
                throw new IllegalArgumentException("Grade must be between 0 and 100");
            }
            if (!body.getTestType().equals("midterm") && !body.getTestType().equals("final")) {
                throw new IllegalArgumentException("Test type must be either midterm or final");
            }
            if (!studentRepository.existsById(body.getStudentId())) {
                throw new IllegalArgumentException("Student does not exist");
            }
            if (!courseRepository.existsById(body.getTestId())) {
                throw new IllegalArgumentException("Course does not exist");
            }
            TestResultEntity testResultEntity = modelMapperService.mapClass(body, TestResultEntity.class);
            testResultRepository.save(testResultEntity);
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
            TestResultEntity testResultEntity = testResultRepository.findByStudentIdAndTestId(studentId, courseId);
            testResultRepository.delete(testResultEntity);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }
}
