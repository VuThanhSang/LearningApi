package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.FileDto;
import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import com.example.learning_api.repository.database.DeadlineRepository;
import com.example.learning_api.repository.database.DeadlineSubmissionsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDeadlineSubmissionsService;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineSubmissionsService implements IDeadlineSubmissionsService {
    private final DeadlineSubmissionsRepository deadlineSubmissionsRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final DeadlineRepository deadlineRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public void CreateDeadlineSubmissions(CreateDeadlineSubmissionsRequest body) {
        try {
            if (body.getDeadlineId() == null) {
                throw new IllegalArgumentException("DeadlineId is required");
            }
            if (deadlineRepository.findById(body.getDeadlineId()) == null) {
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            if (body.getStudentId() == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (studentRepository.findById(body.getStudentId()) == null) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            DeadlineSubmissionsEntity deadlineSubmissionsEntity = modelMapperService.mapClass(body, DeadlineSubmissionsEntity.class);

            List<FileDto> attachments = new ArrayList<>();
            if (body.getFile() != null && !body.getFile().isEmpty()) {
                for (MultipartFile file : body.getFile()) {
                    byte[] fileBytes = file.getBytes();
                    String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(
                            CloudinaryConstant.CLASSROOM_PATH,
                            StringUtils.generateFileName(body.getTitle(), "Resource") + fileType,
                            fileBytes,
                            "raw"
                    );
                    FileDto fileDto = new FileDto();
                    fileDto.setFilePath(response.getSecureUrl());
                    fileDto.setFileName(file.getOriginalFilename());
                    fileDto.setFileExtension(fileType);
                    fileDto.setFileSize(file.getSize() + " bytes");
                    attachments.add(fileDto);

                }
            }
            deadlineSubmissionsEntity.setAttachments(attachments);
            deadlineSubmissionsEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsRepository.save(deadlineSubmissionsEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void UpdateDeadlineSubmissions(UpdateDeadlineSubmissionsRequest body) {
        try {
            DeadlineSubmissionsEntity deadlineSubmissionsEntity = deadlineSubmissionsRepository.findById(body.getId()).orElse(null);
            if (deadlineSubmissionsEntity == null) {
                throw new IllegalArgumentException("DeadlineSubmissionsId is not found");
            }
            if (body.getGrade() != null) {
                deadlineSubmissionsEntity.setGrade(body.getGrade());
            }
            if (body.getFeedback() != null) {
                deadlineSubmissionsEntity.setFeedback(body.getFeedback());
            }
            if (body.getStatus() != null) {
                deadlineSubmissionsEntity.setStatus(DeadlineSubmissionStatus.valueOf(body.getStatus()));
            }

            List<FileDto> attachments = new ArrayList<>();
            if (body.getFile() != null && !body.getFile().isEmpty()) {
                for (MultipartFile file : body.getFile()) {
                    byte[] fileBytes = file.getBytes();
                    String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(
                            CloudinaryConstant.CLASSROOM_PATH,
                            StringUtils.generateFileName(body.getTitle(), "Resource") + fileType,
                            fileBytes,
                            "raw"
                    );
                    FileDto fileDto = new FileDto();
                    fileDto.setFilePath(response.getSecureUrl());
                    fileDto.setFileName(file.getOriginalFilename());
                    fileDto.setFileExtension(fileType);
                    fileDto.setFileSize(file.getSize() + " bytes");
                    attachments.add(fileDto);

                }
            deadlineSubmissionsEntity.setAttachments(attachments);
            }
            deadlineSubmissionsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsRepository.save(deadlineSubmissionsEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void DeleteDeadlineSubmissions(String id) {
        try {
            if (deadlineSubmissionsRepository.findById(id) == null) {
                throw new IllegalArgumentException("DeadlineSubmissionsId is not found");
            }
            deadlineSubmissionsRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public DeadlineSubmissionsEntity GetDeadlineSubmissions(String id) {
        try {
            DeadlineSubmissionsEntity deadlineSubmissionsEntity = deadlineSubmissionsRepository.findById(id).orElse(null);
            if (deadlineSubmissionsEntity == null) {
                throw new IllegalArgumentException("DeadlineSubmissionsId is not found");
            }
            return deadlineSubmissionsEntity;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByDeadlineId(String deadlineId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<DeadlineSubmissionsEntity> deadlineSubmissionsEntities = deadlineSubmissionsRepository.findAllByDeadlineId(deadlineId, pageAble);
            GetDeadlineSubmissionsResponse response = new GetDeadlineSubmissionsResponse();
            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> deadlineSubmissionResponses = new ArrayList<>();
            for (DeadlineSubmissionsEntity deadlineSubmissionsEntity : deadlineSubmissionsEntities) {
                GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse deadlineSubmissionResponse = GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse.fromDeadlineSubmissionEntity(deadlineSubmissionsEntity);
                deadlineSubmissionResponses.add(deadlineSubmissionResponse);
            }
            response.setDeadlineSubmissions(deadlineSubmissionResponses);
            response.setTotalElements(deadlineSubmissionsEntities.getTotalElements());
            response.setTotalPage(deadlineSubmissionsEntities.getTotalPages());
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByStudentId(String studentId,String deadlineId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<DeadlineSubmissionsEntity> deadlineSubmissionsEntities = deadlineSubmissionsRepository.findAllByStudentIdAndDeadlineId(studentId,deadlineId, pageAble);
            GetDeadlineSubmissionsResponse response = new GetDeadlineSubmissionsResponse();
            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> deadlineSubmissionResponses = new ArrayList<>();
            for (DeadlineSubmissionsEntity deadlineSubmissionsEntity : deadlineSubmissionsEntities) {
                GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse deadlineSubmissionResponse = GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse.fromDeadlineSubmissionEntity(deadlineSubmissionsEntity);
                deadlineSubmissionResponses.add(deadlineSubmissionResponse);
            }
            response.setDeadlineSubmissions(deadlineSubmissionResponses);
            response.setTotalElements(deadlineSubmissionsEntities.getTotalElements());
            response.setTotalPage(deadlineSubmissionsEntities.getTotalPages());
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}