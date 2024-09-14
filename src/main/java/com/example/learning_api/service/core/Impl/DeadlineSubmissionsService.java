package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.deadline.DeadlineSubmissionResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.repository.database.DeadlineRepository;
import com.example.learning_api.repository.database.DeadlineSubmissionsRepository;
import com.example.learning_api.repository.database.FileRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDeadlineSubmissionsService;
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
import java.util.Arrays;
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
    private final FileRepository fileRepository;
    public void processFiles (List<MultipartFile> files,String title, DeadlineSubmissionsEntity deadlineSubmissionsEntity) {
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            try {
                FileEntity fileEntity = new FileEntity();
                FAQEntity.SourceDto fileDto = processFile(file, deadlineSubmissionsEntity.getTitle());
                fileEntity.setUrl(fileDto.getPath());
                fileEntity.setType(fileDto.getType().toString());
                fileEntity.setExtension(fileDto.getPath().substring(fileDto.getPath().lastIndexOf(".") + 1));
                fileEntity.setName(title);
                fileEntity.setSize(String.valueOf(file.getSize()));
                fileEntity.setOwnerType(FileOwnerType.DEADLINE_SUBMISSION);
                fileEntity.setOwnerId(deadlineSubmissionsEntity.getId());
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }



    }
    public FAQEntity.SourceDto processFile(MultipartFile file, String title) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = StringUtils.generateFileName(title, "deadline");
        CloudinaryUploadResponse response;

        String contentType = file.getContentType();
        if (contentType.startsWith("image/")) {
            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        } else if (contentType.startsWith("video/")) {
            String videoFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
        } else if (contentType.startsWith("application/")) {
            String docFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
        } else {
            throw new IllegalArgumentException("Unsupported source type");
        }

        return FAQEntity.SourceDto.builder()
                .path(response.getSecureUrl())
                .type(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT)
                .build();

    }
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

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



            deadlineSubmissionsEntity.setGrade("0");
            deadlineSubmissionsEntity.setStatus(DeadlineSubmissionStatus.SUBMITTED);
            deadlineSubmissionsEntity.setFeedback("");
            deadlineSubmissionsEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsRepository.save(deadlineSubmissionsEntity);
            processFiles(body.getFiles(), body.getTitle(), deadlineSubmissionsEntity);
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
            processFiles(body.getFiles(), body.getTitle(), deadlineSubmissionsEntity);
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
    public void GradeDeadlineSubmissions(String id, String grade, String feedback) {
        try {
            DeadlineSubmissionsEntity deadlineSubmissionsEntity = deadlineSubmissionsRepository.findById(id).orElse(null);
            if (deadlineSubmissionsEntity == null) {
                throw new IllegalArgumentException("DeadlineSubmissionsId is not found");
            }
            deadlineSubmissionsEntity.setGrade(grade);
            deadlineSubmissionsEntity.setFeedback(feedback);
            deadlineSubmissionsEntity.setStatus(DeadlineSubmissionStatus.GRADED);
            deadlineSubmissionsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineSubmissionsRepository.save(deadlineSubmissionsEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public DeadlineSubmissionResponse GetDeadlineSubmissions(String id) {
        try {
            DeadlineSubmissionsEntity deadlineSubmissionsEntity = deadlineSubmissionsRepository.findById(id).orElse(null);
            DeadlineSubmissionResponse deadlineSubmissionResponse = modelMapperService.mapClass(deadlineSubmissionsEntity, DeadlineSubmissionResponse.class);
            deadlineSubmissionResponse.setFiles(fileRepository.findByOwnerIdAndOwnerType(id, FileOwnerType.DEADLINE_SUBMISSION.name()));
            if (deadlineSubmissionsEntity == null) {
                throw new IllegalArgumentException("DeadlineSubmissionsId is not found");
            }
            return deadlineSubmissionResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByDeadlineId(
            String deadlineId, Integer page, Integer size, String search, String status,
            String sortBy, Sort.Direction sortDirection) {
        try {
            if (status=="," || status==null){
                status=null;
            }
            // Validate and sanitize sortBy
            List<String> allowedSortFields = Arrays.asList("createdAt", "updatedAt", "studentName", "status");
            if (sortBy == null || !allowedSortFields.contains(sortBy)) {
                sortBy = "createdAt";  // Default to createdAt if invalid or null
            }

            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Ensure search is never null
            search = (search == null) ? "" : search;

            // Use a single query method that can handle null parameters
            Page<DeadlineSubmissionsEntity> deadlineSubmissionsEntities = deadlineSubmissionsRepository
                    .findAllByDeadlineIdWithFilters(deadlineId, search, status, pageable);


            GetDeadlineSubmissionsResponse response = new GetDeadlineSubmissionsResponse();
            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> deadlineSubmissionResponses = new ArrayList<>();

            for (DeadlineSubmissionsEntity entity : deadlineSubmissionsEntities) {
                GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse submissionResponse =
                        fromDeadlineSubmissionEntity(entity);

                StudentEntity studentEntity = studentRepository.findById(entity.getStudentId()).orElse(null);
                if (studentEntity != null) {
                    submissionResponse.setStudentName(studentEntity.getUser().getFullname());
                    submissionResponse.setStudentEmail(studentEntity.getUser().getEmail());
                    submissionResponse.setStudentAvatar(studentEntity.getUser().getAvatar());
                }
                deadlineSubmissionResponses.add(submissionResponse);
            }
            long totalElement = deadlineSubmissionsRepository.countAllByDeadlineIdWithFilters(deadlineId, search, status);
            int totalPage = (int) Math.ceil((double) totalElement / size);
            response.setDeadlineSubmissions(deadlineSubmissionResponses);
            response.setTotalElements(totalElement);
            response.setTotalPage(totalPage);
            return response;
        } catch (Exception e) {
            log.error("Error in GetDeadlineSubmissionsByDeadlineId: ", e);
            throw new IllegalArgumentException("Error retrieving deadline submissions: " + e.getMessage());
        }


    }
    public GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse fromDeadlineSubmissionEntity(DeadlineSubmissionsEntity entity) {
        GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse response = new GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse();
        response.setFiles(fileRepository.findByOwnerIdAndOwnerType(entity.getId(), FileOwnerType.DEADLINE_SUBMISSION.name()));
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDeadlineId(entity.getDeadlineId());
        response.setStudentId(entity.getStudentId());
        response.setSubmission(entity.getSubmission());
        response.setGrade(entity.getGrade());
        response.setFeedback(entity.getFeedback());
        response.setStatus(entity.getStatus().name());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
    @Override
    public GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByStudentId(String studentId,String deadlineId, Integer page, Integer size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<DeadlineSubmissionsEntity> deadlineSubmissionsEntities = deadlineSubmissionsRepository.findAllByStudentIdAndDeadlineId(studentId,deadlineId, pageAble);
            GetDeadlineSubmissionsResponse response = new GetDeadlineSubmissionsResponse();
            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> deadlineSubmissionResponses = new ArrayList<>();
            for (DeadlineSubmissionsEntity deadlineSubmissionsEntity : deadlineSubmissionsEntities) {
                GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse deadlineSubmissionResponse = fromDeadlineSubmissionEntity(deadlineSubmissionsEntity);
                StudentEntity studentEntity = studentRepository.findById(deadlineSubmissionsEntity.getStudentId()).orElse(null);
                deadlineSubmissionResponse.setStudentEmail(studentEntity.getUser().getEmail());
                deadlineSubmissionResponse.setStudentName(studentEntity.getUser().getFullname());
                deadlineSubmissionResponse.setStudentAvatar(studentEntity.getUser().getAvatar());
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
    public List<String> downloadSubmission(String deadlineId, DeadlineSubmissionStatus type) {
        try {
            List<DeadlineSubmissionsEntity> deadlineSubmissionsEntities = deadlineSubmissionsRepository.findAllByDeadlineIdAndStatus(deadlineId, type);
            List<String> urls = new ArrayList<>();
            for (DeadlineSubmissionsEntity deadlineSubmissionsEntity : deadlineSubmissionsEntities) {
                List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(deadlineSubmissionsEntity.getId(), FileOwnerType.DEADLINE_SUBMISSION.name());
                for (FileEntity fileEntity : fileEntities) {
                    urls.add(fileEntity.getUrl());
                }
            }
            return urls;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}