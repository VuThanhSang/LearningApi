package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.feedback.CreateFeedbackAnswerRequest;
import com.example.learning_api.dto.request.feedback.CreateFeedbackRequest;
import com.example.learning_api.dto.request.feedback.UpdateFeedbackRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.feedback.GetFeedBacksResponse;
import com.example.learning_api.dto.response.feedback.FeedbackAnswerResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.FeedbackFormType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFeedbackService;
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
public class FeedbackService implements IFeedbackService {
    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final TestRepository testRepository;
    private final DeadlineRepository deadlinerepository;
    private final CloudinaryService cloudinaryService;
    private final TeacherRepository teacherRepository;
    private final FileRepository fileRepository;
    @Override
    public void createFeedback(CreateFeedbackRequest body) {
        if (body.getFormId() == null) {
            throw new RuntimeException("TestId is required");
        }
        if (body.getStudentId() == null) {
            throw new RuntimeException("StudentId is required");
        }
       if (body.getFormType().equals(FeedbackFormType.DEADLINE)){
           if (deadlinerepository.findById(body.getFormId()).isEmpty()) {
               throw new RuntimeException("DeadlineId not found");
           }
       }else {
           if (testRepository.findById(body.getFormId()).isEmpty()) {
               throw new RuntimeException("TestId not found");
           }
       }
        if (studentRepository.findById(body.getStudentId()).isEmpty()) {
            throw new RuntimeException("StudentId not found");
        }
        FeedbackEntity feedbackEntity = modelMapperService.mapClass(body, FeedbackEntity.class);
        feedbackEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackRepository.save(feedbackEntity);
        progressSources(body.getSources(), body.getFeedback(), feedbackEntity);

    }

    public void progressSources(List<MultipartFile> sources,String feedback,FeedbackEntity feedbackEntity){
        if (sources == null) {
            return;

        }
        for (MultipartFile source : sources) {
            try {
                String sourceDto = processSource(source, feedback);
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(sourceDto);
                fileEntity.setName(source.getOriginalFilename());
                fileEntity.setType("IMAGE");
                fileEntity.setOwnerType(FileOwnerType.FEEDBACK);
                fileEntity.setOwnerId(feedbackEntity.getId());
                fileEntity.setExtension("jpg");
                fileEntity.setSize(String.valueOf(source.getSize()));
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
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
    public void updateFeedback(UpdateFeedbackRequest body) {
            if (body.getId() == null) {
            throw new RuntimeException("Id is required");
        }
        if (feedbackRepository.findById(body.getId()).isEmpty()) {
            throw new RuntimeException("Feedback not found");
        }
        FeedbackEntity feedbackEntity = feedbackRepository.findById(body.getId()).orElseThrow(() -> new RuntimeException("Feedback not found"));
        if (body.getSources() != null) {
            progressSources(body.getSources(), body.getFeedback(), feedbackEntity);
        }
        if (body.getTitle() != null)
            feedbackEntity.setTitle(body.getTitle());
        if (body.getFeedback() != null)
            feedbackEntity.setFeedback(body.getFeedback());
        feedbackEntity.setFeedback(body.getFeedback());
        feedbackEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackRepository.save(feedbackEntity);
    }

    @Override
    public void deleteFeedback(String feedbackId) {
        if (feedbackRepository.findById(feedbackId).isEmpty()) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(feedbackId);

    }

    @Override
    public void createFeedbackAnswer(CreateFeedbackAnswerRequest body) {
        if (body.getFeedbackId() == null) {
            throw new RuntimeException("FeedbackId is required");
        }

        if (feedbackRepository.findById(body.getFeedbackId()).isEmpty()) {
            throw new RuntimeException("FeedbackId not found");
        }
        FeedbackAnswerEntity feedbackAnswerEntity = modelMapperService.mapClass(body, FeedbackAnswerEntity.class);
        feedbackAnswerEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackAnswerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackAnswerRepository.save(feedbackAnswerEntity);
    }

    @Override
    public void updateFeedbackAnswer(String feedbackAnswerId, String answer) {
        if (feedbackAnswerRepository.findById(feedbackAnswerId).isEmpty()) {
            throw new RuntimeException("FeedbackAnswer not found");
        }
        FeedbackAnswerEntity feedbackAnswerEntity = feedbackAnswerRepository.findById(feedbackAnswerId).orElseThrow(() -> new RuntimeException("FeedbackAnswer not found"));
        if (answer != null)
            feedbackAnswerEntity.setAnswer(answer);
        feedbackAnswerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        feedbackAnswerRepository.save(feedbackAnswerEntity);
    }

    @Override
    public void deleteFeedbackAnswer(String feedbackAnswerId) {

        if (feedbackAnswerRepository.findById(feedbackAnswerId).isEmpty()) {
            throw new RuntimeException("FeedbackAnswer not found");
        }
        feedbackAnswerRepository.deleteById(feedbackAnswerId);
    }

    @Override
    public FeedbackEntity getFeedbackById(String feedbackId) {
        if (feedbackId == null) {
            throw new RuntimeException("FeedbackId is required");
        }
        FeedbackEntity data= feedbackRepository.findById(feedbackId).orElseThrow(() -> new RuntimeException("FeedbackId not found"));
        data.setFiles(fileRepository.findByOwnerIdAndOwnerType(feedbackId, FileOwnerType.FEEDBACK.name()));
        List<FeedbackAnswerEntity> answers = feedbackAnswerRepository.findByFeedbackId(feedbackId);
        for (FeedbackAnswerEntity feedbackAnswerEntity : answers) {
            feedbackAnswerEntity.setTeacher(teacherRepository.findById(feedbackAnswerEntity.getTeacherId()).orElse(null));

        }
        data.setStudent(studentRepository.findById(data.getStudentId()).orElse(null));
        data.setAnswers(answers);
        return data;
    }

    @Override
    public List<FeedbackEntity> getFeedbacksByStudentIdAndTestId(String studentId, String formId,String formType) {
        try {
            if (studentId == null) {
                throw new RuntimeException("StudentId is required");
            }
            if (formId == null) {
                throw new RuntimeException("TestId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new RuntimeException("StudentId not found");
            }
            if (testRepository.findById(formId).isEmpty()) {
                throw new RuntimeException("TestId not found");
            }
            List<FeedbackEntity> data= feedbackRepository.findByStudentIdAndFormIdAndFormType(studentId, formId,formType);
            for (FeedbackEntity feedbackEntity : data) {
                feedbackEntity.setFiles(fileRepository.findByOwnerIdAndOwnerType(feedbackEntity.getId(), FileOwnerType.FEEDBACK.name()));
                List<FeedbackAnswerEntity> answers = feedbackAnswerRepository.findByFeedbackId(feedbackEntity.getId());
                for (FeedbackAnswerEntity feedbackAnswerEntity : answers) {
                    feedbackAnswerEntity.setTeacher(teacherRepository.findById(feedbackAnswerEntity.getTeacherId()).orElse(null));
                }
                feedbackEntity.setStudent(studentRepository.findById(feedbackEntity.getStudentId()).orElse(null));
                feedbackEntity.setAnswers(answers);
            }
            return data;
        } catch (Exception e) {
            log.error("Error getting feedbacks: " + e.getMessage());
            throw new RuntimeException("Error getting feedbacks");
        }
    }

    @Override
    public GetFeedBacksResponse getFeedbacksByTestId(String formId,String formType, String sort, int page, int size) {
        try {
            if (formId == null) {
                throw new RuntimeException("TestId is required");
            }
            if (testRepository.findById(formId).isEmpty()) {
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
            Page<FeedbackEntity> feedbackEntities = feedbackRepository.findByFormIdAndFormType(formId,formType, pageable);
            for (FeedbackEntity feedbackEntity : feedbackEntities) {
                feedbackEntity.setFiles(fileRepository.findByOwnerIdAndOwnerType(feedbackEntity.getId(), FileOwnerType.FEEDBACK.name()));
                List<FeedbackAnswerEntity> answers = feedbackAnswerRepository.findByFeedbackId(feedbackEntity.getId());
                for (FeedbackAnswerEntity feedbackAnswerEntity : answers) {
                    feedbackAnswerEntity.setTeacher(teacherRepository.findById(feedbackAnswerEntity.getTeacherId()).orElse(null));
                }
                feedbackEntity.setStudent(studentRepository.findById(feedbackEntity.getStudentId()).orElse(null));
                feedbackEntity.setAnswers(answers);
            }
            GetFeedBacksResponse response = new GetFeedBacksResponse();
            response.setFeedbacks(feedbackEntities.getContent());
            response.setTotalPage(feedbackEntities.getTotalPages());
            response.setTotalElements(feedbackEntities.getTotalElements());
            return response;
        } catch (Exception e) {
            log.error("Error getting feedbacks: " + e.getMessage());
            throw new RuntimeException("Error getting feedbacks");
        }
    }


    @Override
    public List<FeedbackAnswerResponse> getFeedbackAnswersByFeedbackId(String feedbackId) {
        try {
            if (feedbackId == null) {
                throw new RuntimeException("FeedbackId is required");
            }

           List<FeedbackAnswerEntity> data =  feedbackAnswerRepository.findByFeedbackId(feedbackId);
            for (FeedbackAnswerEntity feedbackAnswerEntity : data) {

                feedbackAnswerEntity.setTeacher(teacherRepository.findById(feedbackAnswerEntity.getTeacherId()).orElse(null));
            }
            List<FeedbackAnswerResponse> response = new ArrayList<>();
            for (FeedbackAnswerEntity feedbackAnswerEntity : data) {
                FeedbackAnswerResponse answerResponse = modelMapperService.mapClass(feedbackAnswerEntity, FeedbackAnswerResponse.class);
                response.add(answerResponse);
            }
            return response;
        } catch (Exception e) {
            log.error("Error getting feedback answers: " + e.getMessage());
            throw new RuntimeException("Error getting feedback answers");
        }
    }
}
