package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.ForumEntity;
import com.example.learning_api.entity.sql.database.TermsEntity;
import com.example.learning_api.entity.sql.database.TestFeedbackAnswerEntity;
import com.example.learning_api.entity.sql.database.TestFeedbackEntity;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TestFeedbackAnswerRepository;
import com.example.learning_api.repository.database.TestFeedbackRepository;
import com.example.learning_api.repository.database.TestRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestFeedbackService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackService implements ITestFeedbackService {
    private final TestFeedbackAnswerRepository testFeedbackAnswerRepository;
    private final TestFeedbackRepository testFeedbackRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final TestRepository testRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public void createTestFeedback(CreateTestFeedbackRequest body) {
        if (body.getTestId() == null) {
            throw new RuntimeException("TestId is required");
        }
        if (body.getStudentId() == null) {
            throw new RuntimeException("StudentId is required");
        }
        if (testRepository.findById(body.getTestId()).isEmpty()) {
            throw new RuntimeException("TestId not found");
        }
        if (studentRepository.findById(body.getStudentId()).isEmpty()) {
            throw new RuntimeException("StudentId not found");
        }
        TestFeedbackEntity testFeedbackEntity = modelMapperService.mapClass(body, TestFeedbackEntity.class);
        progressSources(body.getSources(), body.getFeedback(), testFeedbackEntity);
        testFeedbackEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackRepository.save(testFeedbackEntity);

    }

    public void progressSources(List<MultipartFile> sources,String feedback,TestFeedbackEntity testFeedbackEntity){
        if (sources == null) {
            return;

        }
        for (MultipartFile source : sources) {
            try {
                String sourceDto = processSource(source, feedback);
                testFeedbackEntity.getSources().add(sourceDto);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }

    }
    private String processSource(MultipartFile source, String question) throws IOException {
        byte[] fileBytes = source.getBytes();
        String fileName = StringUtils.generateFileName(question, "forum");
        CloudinaryUploadResponse response;
        byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        return response.getUrl();
    }

    @Override
    public void updateTestFeedback(UpdateTestFeedbackRequest body) {
        if (body.getId() == null) {
            throw new RuntimeException("Id is required");
        }
        TestFeedbackEntity testFeedbackEntity = testFeedbackRepository.findById(body.getId()).orElseThrow(() -> new RuntimeException("TestFeedback not found"));
        if (body.getSources() != null) {
            testFeedbackEntity.getSources().clear();
            progressSources(body.getSources(), body.getFeedback(), testFeedbackEntity);
        }
        testFeedbackEntity.setFeedback(body.getFeedback());
        testFeedbackEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackRepository.save(testFeedbackEntity);
    }

    @Override
    public void deleteTestFeedback(String testFeedbackId) {
        if (testFeedbackRepository.findById(testFeedbackId).isEmpty()) {
            throw new RuntimeException("TestFeedback not found");
        }
        testFeedbackRepository.deleteById(testFeedbackId);

    }

    @Override
    public void createTestFeedbackAnswer(CreateTestFeedbackAnswerRequest body) {
        if (body.getTestFeedbackId() == null) {
            throw new RuntimeException("TestFeedbackId is required");
        }

        if (testFeedbackRepository.findById(body.getTestFeedbackId()).isEmpty()) {
            throw new RuntimeException("TestFeedbackId not found");
        }
        TestFeedbackAnswerEntity testFeedbackAnswerEntity = modelMapperService.mapClass(body, TestFeedbackAnswerEntity.class);
        testFeedbackAnswerEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackAnswerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackAnswerRepository.save(testFeedbackAnswerEntity);
    }

    @Override
    public void updateTestFeedbackAnswer(String testFeedbackAnswerId, String answer) {
        if (testFeedbackAnswerRepository.findById(testFeedbackAnswerId).isEmpty()) {
            throw new RuntimeException("TestFeedbackAnswer not found");
        }
        TestFeedbackAnswerEntity testFeedbackAnswerEntity = testFeedbackAnswerRepository.findById(testFeedbackAnswerId).orElseThrow(() -> new RuntimeException("TestFeedbackAnswer not found"));
        testFeedbackAnswerEntity.setAnswer(answer);
        testFeedbackAnswerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        testFeedbackAnswerRepository.save(testFeedbackAnswerEntity);
    }

    @Override
    public void deleteTestFeedbackAnswer(String testFeedbackAnswerId) {

        if (testFeedbackAnswerRepository.findById(testFeedbackAnswerId).isEmpty()) {
            throw new RuntimeException("TestFeedbackAnswer not found");
        }
        testFeedbackAnswerRepository.deleteById(testFeedbackAnswerId);
    }
}
