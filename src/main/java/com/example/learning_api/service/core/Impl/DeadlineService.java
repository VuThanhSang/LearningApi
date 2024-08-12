package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.deadline.CreateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlinesResponse;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineService implements IDeadlineService {
    private final DeadlineRepository deadlineRepository;
    private final LessonRepository lessonRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    private final ClassRoomRepository classroomRepository;

    @Override
    public void createDeadline(CreateDeadlineRequest body) {
        try {
            if (body.getLessonId()==null){
                throw new IllegalArgumentException("ClassroomId is required");
            }
            if (lessonRepository.findById(body.getLessonId()) == null){
                throw new IllegalArgumentException("lessonId is not found");
            }


            DeadlineEntity deadlineEntity = modelMapperService.mapClass(body, DeadlineEntity.class);
            if (body.getFile()!=null){
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getTitle(), "Resource") + fileType,
                        fileBytes,
                        "raw"
                );
                deadlineEntity.setAttachment(response.getSecureUrl());
            }
            deadlineEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineEntity.setStatus(DeadlineStatus.UPCOMING);
            deadlineRepository.save(deadlineEntity);
        } catch (Exception e) {
            log.error("Error in createDeadline: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateDeadline(UpdateDeadlineRequest body) {
        try{
            DeadlineEntity deadlineEntity = deadlineRepository.findById(body.getId()).orElse(null);
            if (deadlineEntity == null){
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            if (body.getTitle()!=null){
                deadlineEntity.setTitle(body.getTitle());
            }
            if (body.getDescription()!=null){
                deadlineEntity.setDescription(body.getDescription());
            }
            if (body.getStartDate()!=null){
                deadlineEntity.setStartDate(body.getStartDate());
            }
            if (body.getEndDate()!=null){
                deadlineEntity.setEndDate(body.getEndDate());
            }
            if (body.getType()!=null){
                deadlineEntity.setType(DeadlineType.valueOf(body.getType()));
            }
            if (body.getStatus()!=null){
                deadlineEntity.setStatus(DeadlineStatus.valueOf(body.getStatus()));
            }

            if (body.getFile()!=null){
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getTitle(), "Resource") + fileType,
                        fileBytes,
                        "raw"
                );
                deadlineEntity.setAttachment(response.getSecureUrl());
            }
            deadlineEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineRepository.save(deadlineEntity);
        }
        catch (Exception e) {
            log.error("Error in updateDeadline: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteDeadline(String deadlineId) {
        try{
            DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId).orElse(null);
            if (deadlineEntity == null){
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            deadlineRepository.deleteById(deadlineId);
        }
        catch (Exception e) {
            log.error("Error in deleteDeadline: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public DeadlineEntity getDeadline(String deadlineId) {
        try{
            DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId).orElse(null);
            if (deadlineEntity == null){
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            return deadlineEntity;
        }
        catch (Exception e) {
            log.error("Error in getDeadline: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetDeadlinesResponse getDeadlinesByClassroomId(String lessonId, Integer page, Integer size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<DeadlineEntity> deadlineEntities = deadlineRepository.findAllByLessonId(lessonId, pageAble);
            GetDeadlinesResponse response = new GetDeadlinesResponse();
            List<GetDeadlinesResponse.DeadlineResponse> deadlineResponses = new ArrayList<>();
            for (DeadlineEntity deadlineEntity : deadlineEntities){
                GetDeadlinesResponse.DeadlineResponse deadlineResponse = GetDeadlinesResponse.DeadlineResponse.fromDeadlineEntity(deadlineEntity);
                deadlineResponses.add(deadlineResponse);
            }
            response.setTotalElements(deadlineEntities.getTotalElements());
            response.setTotalPage(deadlineEntities.getTotalPages());
            response.setDeadlines(deadlineResponses);
            return response;

        }
        catch (Exception e) {
            log.error("Error in getDeadlinesByClassroomId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<UpcomingDeadlinesResponse> getUpcomingDeadlineByStudentId(String studentId, String date) {
        try{
            if (studentRepository.findById(studentId) == null){
                throw new IllegalArgumentException("StudentId is not found");
            }
            List<UpcomingDeadlinesResponse> updateDeadlineRequest = studentEnrollmentsRepository.getUpcomingDeadlines(studentId, date);
            return updateDeadlineRequest;
        }
        catch (Exception e) {
            log.error("Error in convertToDeadlineRequest: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<ClassroomDeadlineResponse> getClassroomDeadlinesByClassroomId(String classroomId) {
        try{
            if (classroomRepository.findById(classroomId) == null){
                throw new IllegalArgumentException("ClassroomId is not found");
            }
            List<ClassroomDeadlineResponse> classroomDeadlineResponses = classroomRepository.getDeadlinesForClassroom(classroomId);
            return classroomDeadlineResponses;
        }
        catch (Exception e) {
            log.error("Error in convertToDeadlineRequest: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
