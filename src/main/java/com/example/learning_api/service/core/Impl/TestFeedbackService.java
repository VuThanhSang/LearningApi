package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.test_feedback.GetTestFeedBacksResponse;
import com.example.learning_api.dto.response.test_feedback.TestFeedbackAnswerResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestFeedbackService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    private final TeacherRepository teacherRepository;
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
        testFeedbackEntity.setSources(new ArrayList<>());
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
        if (testFeedbackRepository.findById(body.getId()).isEmpty()) {
            throw new RuntimeException("TestFeedback not found");
        }
        TestFeedbackEntity testFeedbackEntity = testFeedbackRepository.findById(body.getId()).orElseThrow(() -> new RuntimeException("TestFeedback not found"));
        if (body.getSources() != null) {
            testFeedbackEntity.getSources().clear();
            progressSources(body.getSources(), body.getFeedback(), testFeedbackEntity);
        }
        if (body.getTitle() != null)
            testFeedbackEntity.setTitle(body.getTitle());
        if (body.getFeedback() != null)
            testFeedbackEntity.setFeedback(body.getFeedback());
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
        if (answer != null)
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

    @Override
    public TestFeedbackEntity getTestFeedbackById(String testFeedbackId) {
        if (testFeedbackId == null) {
            throw new RuntimeException("TestFeedbackId is required");
        }
        TestFeedbackEntity data= testFeedbackRepository.findById(testFeedbackId).orElseThrow(() -> new RuntimeException("TestFeedbackId not found"));
        List<TestFeedbackAnswerEntity> answers = testFeedbackAnswerRepository.findByTestFeedbackId(testFeedbackId);
        for (TestFeedbackAnswerEntity testFeedbackAnswerEntity : answers) {
            testFeedbackAnswerEntity.setTeacher(teacherRepository.findById(testFeedbackAnswerEntity.getTeacherId()).orElse(null));
        }
        data.setStudent(studentRepository.findById(data.getStudentId()).orElse(null));
        data.setAnswers(answers);
        return data;
    }

    @Override
    public List<TestFeedbackEntity> getTestFeedbacksByStudentIdAndTestId(String studentId, String testId) {
        try {
            if (studentId == null) {
                throw new RuntimeException("StudentId is required");
            }
            if (testId == null) {
                throw new RuntimeException("TestId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new RuntimeException("StudentId not found");
            }
            if (testRepository.findById(testId).isEmpty()) {
                throw new RuntimeException("TestId not found");
            }
            List<TestFeedbackEntity> data= testFeedbackRepository.findByStudentIdAndTestId(studentId, testId);
            for (TestFeedbackEntity testFeedbackEntity : data) {
                List<TestFeedbackAnswerEntity> answers = testFeedbackAnswerRepository.findByTestFeedbackId(testFeedbackEntity.getId());
                for (TestFeedbackAnswerEntity testFeedbackAnswerEntity : answers) {
                    testFeedbackAnswerEntity.setTeacher(teacherRepository.findById(testFeedbackAnswerEntity.getTeacherId()).orElse(null));
                }
                testFeedbackEntity.setStudent(studentRepository.findById(testFeedbackEntity.getStudentId()).orElse(null));
                testFeedbackEntity.setAnswers(answers);
            }
            return data;
        } catch (Exception e) {
            log.error("Error getting test feedbacks: " + e.getMessage());
            throw new RuntimeException("Error getting test feedbacks");
        }
    }

    @Override
    public GetTestFeedBacksResponse getTestFeedbacksByTestId(String testId, String sort, int page, int size) {
        try {
            if (testId == null) {
                throw new RuntimeException("TestId is required");
            }
            if (testRepository.findById(testId).isEmpty()) {
                throw new RuntimeException("TestId not found");
            }

            // Xử lý sắp xếp
            Sort sortOrder = Sort.unsorted();
            if (sort != null) {
                if (sort.equalsIgnoreCase("asc")) {
                    sortOrder = Sort.by(Sort.Direction.ASC, "createdAt");
                } else if (sort.equalsIgnoreCase("desc")) {
                    sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
                }
            }

            Pageable pageable = PageRequest.of(page, size, sortOrder);
            Page<TestFeedbackEntity> testFeedbackEntities = testFeedbackRepository.findByTestId(testId, pageable);
            for (TestFeedbackEntity testFeedbackEntity : testFeedbackEntities) {
                List<TestFeedbackAnswerEntity> answers = testFeedbackAnswerRepository.findByTestFeedbackId(testFeedbackEntity.getId());
                for (TestFeedbackAnswerEntity testFeedbackAnswerEntity : answers) {
                    testFeedbackAnswerEntity.setTeacher(teacherRepository.findById(testFeedbackAnswerEntity.getTeacherId()).orElse(null));
                }
                testFeedbackEntity.setStudent(studentRepository.findById(testFeedbackEntity.getStudentId()).orElse(null));
                testFeedbackEntity.setAnswers(answers);
            }
            GetTestFeedBacksResponse response = new GetTestFeedBacksResponse();
            response.setTestFeedbacks(testFeedbackEntities.getContent());
            response.setTotalPage(testFeedbackEntities.getTotalPages());
            response.setTotalElements(testFeedbackEntities.getTotalElements());
            return response;
        } catch (Exception e) {
            log.error("Error getting test feedbacks: " + e.getMessage());
            throw new RuntimeException("Error getting test feedbacks");
        }
    }


    @Override
    public List<TestFeedbackAnswerResponse> getTestFeedbackAnswersByFeedbackId(String testFeedbackId) {
        try {
            if (testFeedbackId == null) {
                throw new RuntimeException("TestFeedbackId is required");
            }

           List<TestFeedbackAnswerEntity> data =  testFeedbackAnswerRepository.findByTestFeedbackId(testFeedbackId);
            for (TestFeedbackAnswerEntity testFeedbackAnswerEntity : data) {
                testFeedbackAnswerEntity.setTeacher(teacherRepository.findById(testFeedbackAnswerEntity.getTeacherId()).orElse(null));
            }
            List<TestFeedbackAnswerResponse> response = new ArrayList<>();
            for (TestFeedbackAnswerEntity testFeedbackAnswerEntity : data) {
                TestFeedbackAnswerResponse answerResponse = modelMapperService.mapClass(testFeedbackAnswerEntity, TestFeedbackAnswerResponse.class);
                response.add(answerResponse);
            }
            return response;
        } catch (Exception e) {
            log.error("Error getting test feedback answers: " + e.getMessage());
            throw new RuntimeException("Error getting test feedback answers");
        }
    }
}
