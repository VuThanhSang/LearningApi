package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.deadline.DeadlineSubmissionResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.repository.database.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class  DeadlineSubmissionsService implements IDeadlineSubmissionsService {
    private final DeadlineSubmissionsRepository deadlineSubmissionsRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final DeadlineRepository deadlineRepository;
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
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
                fileEntity.setName(file.getOriginalFilename());
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
        String fileName = StringUtils.generateFileName(file.getOriginalFilename(), "deadline");
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
            DeadlineEntity deadlineEntity = deadlineRepository.findById(body.getDeadlineId()).orElse(null);
            if (body.getDeadlineId() == null) {
                throw new IllegalArgumentException("DeadlineId is required");
            }
            if (deadlineEntity == null) {
                throw new IllegalArgumentException("DeadlineId is not found");
            }

            if (body.getStudentId() == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (studentRepository.findById(body.getStudentId()) == null) {
                throw new IllegalArgumentException("StudentId is not found");
            }

            DeadlineSubmissionsEntity deadlineSubmissionsEntity = modelMapperService.mapClass(body, DeadlineSubmissionsEntity.class);
            long endDate = Long.parseLong(deadlineEntity.getEndDate());
            long currentTime = System.currentTimeMillis();
            long gracePeriod = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

            if (currentTime > endDate + gracePeriod) {
                throw new IllegalArgumentException("The deadline and grace period have passed. Submissions are no longer accepted.");
            }

            if (currentTime > endDate) {
                deadlineSubmissionsEntity.setIsLate(true);
            } else {
                deadlineSubmissionsEntity.setIsLate(false);
            }

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
            // Validate deadline exists
            DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId)
                    .orElseThrow(() -> new IllegalArgumentException("DeadlineId is not found"));

            // Clean up status parameter
            if (status != null && (status.isEmpty() || status.equals(","))) {
                status = null;
            }
            if(status !=null)
                status = status.split(",")[1];
            // Validate and sanitize sortBy
            List<String> allowedSortFields = Arrays.asList("createdAt", "updatedAt", "studentName", "status");
            if (sortBy == null || !allowedSortFields.contains(sortBy)) {
                sortBy = "createdAt";
            }

            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            search = (search == null) ? "" : search;

            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> allSubmissions = new ArrayList<>();

            // If status is specifically NOT_SUBMITTED or no status filter is applied
            if (status == null || DeadlineSubmissionStatus.NOT_SUBMITTED.toString().equals(status)) {
                // Get all enrolled students
                List<StudentEnrollmentsEntity> enrolledStudents = studentEnrollmentsRepository
                        .findByClassroomId(deadlineEntity.getClassroomId());

                // Get existing submissions
                List<DeadlineSubmissionsEntity> existingSubmissions = deadlineSubmissionsRepository
                        .findAllByDeadlineIdWithFilters(deadlineId, search, null, Pageable.unpaged());

                // Create set of students who have submitted
                Set<String> submittedStudentIds = existingSubmissions.stream()
                        .map(DeadlineSubmissionsEntity::getStudentId)
                        .collect(Collectors.toSet());

                // Add non-submitted students to the list
                for (StudentEnrollmentsEntity enrollment : enrolledStudents) {
                    if (!submittedStudentIds.contains(enrollment.getStudentId())) {
                        StudentEntity studentEntity = studentRepository.findById(enrollment.getStudentId()).orElse(null);
                        if (studentEntity != null && studentEntity.getUser() != null) {
                            // Only add if the search term matches student name (if search is provided)
                            String studentName = studentEntity.getUser().getFullname();
                            if (search.isEmpty() || studentName.toLowerCase().contains(search.toLowerCase())) {
                                GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse submissionResponse =
                                        new GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse();

                                submissionResponse.setStudentId(enrollment.getStudentId());
                                submissionResponse.setDeadlineId(deadlineId);
                                submissionResponse.setStatus(DeadlineSubmissionStatus.NOT_SUBMITTED.toString());
                                submissionResponse.setStudentName(studentName);
                                submissionResponse.setStudentEmail(studentEntity.getUser().getEmail());
                                submissionResponse.setStudentAvatar(studentEntity.getUser().getAvatar());
                                submissionResponse.setCreatedAt(enrollment.getCreatedAt());
                                submissionResponse.setUpdatedAt(enrollment.getUpdatedAt());

                                allSubmissions.add(submissionResponse);
                            }
                        }
                    }
                }
            }

            // If status is not specifically NOT_SUBMITTED, get submitted assignments
            if (status == null || !DeadlineSubmissionStatus.NOT_SUBMITTED.toString().equals(status)) {
                // Convert status to enum value for submitted assignments
                String submissionStatus = null;
                if (status != null && !status.equals(DeadlineSubmissionStatus.NOT_SUBMITTED.toString())) {
                    try {
                        DeadlineSubmissionStatus.valueOf(status);
                        submissionStatus = status;
                    } catch (IllegalArgumentException e) {
                        // Invalid status, ignore it
                        log.warn("Invalid status value received: {}", status);
                    }
                }

                List<DeadlineSubmissionsEntity> submittedAssignments = deadlineSubmissionsRepository
                        .findAllByDeadlineIdWithFilters(deadlineId, search, submissionStatus, Pageable.unpaged());

                // Add submitted assignments to the list
                for (DeadlineSubmissionsEntity entity : submittedAssignments) {
                    GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse submissionResponse =
                            fromDeadlineSubmissionEntity(entity);

                    StudentEntity studentEntity = studentRepository.findById(entity.getStudentId()).orElse(null);
                    if (studentEntity != null) {
                        submissionResponse.setStudentName(studentEntity.getUser().getFullname());
                        submissionResponse.setStudentEmail(studentEntity.getUser().getEmail());
                        submissionResponse.setStudentAvatar(studentEntity.getUser().getAvatar());
                    }
                    allSubmissions.add(submissionResponse);
                }
            }

            // Sort the combined list
            Comparator<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> comparator;
            switch (sortBy) {
                case "studentName":
                    comparator = Comparator.comparing(
                            GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse::getStudentName,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    break;
                case "status":
                    comparator = Comparator.comparing(
                            submission -> submission.getStatus().toString(),
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    break;
                case "updatedAt":
                    comparator = Comparator.comparing(
                            GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse::getUpdatedAt,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    break;
                default: // createdAt
                    comparator = Comparator.comparing(
                            GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse::getCreatedAt,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            }

            if (sortDirection == Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }

            allSubmissions.sort(comparator);

            // Handle pagination
            int start = page * size;
            int end = Math.min((start + size), allSubmissions.size());
            List<GetDeadlineSubmissionsResponse.DeadlineSubmissionResponse> paginatedSubmissions =
                    allSubmissions.subList(start, end);

            // Create response
            GetDeadlineSubmissionsResponse response = new GetDeadlineSubmissionsResponse();
            response.setDeadlineSubmissions(paginatedSubmissions);
            response.setTotalElements((long) allSubmissions.size());
            response.setTotalPage((int) Math.ceil((double) allSubmissions.size() / size));

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
        response.setIsLate(entity.getIsLate());
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