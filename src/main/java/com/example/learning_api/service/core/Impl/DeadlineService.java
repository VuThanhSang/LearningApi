package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.deadline.CreateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlinesResponse;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.entity.sql.database.SectionEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final SectionRepository sectionRepository;
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
            LessonEntity lessonEntity = lessonRepository.findById(body.getLessonId()).orElse(null);

            SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).orElse(null);
            ClassRoomEntity classRoomEntity = classroomRepository.findById(sectionEntity.getClassRoomId()).orElse(null);
            deadlineEntity.setClassroomId(classRoomEntity.getId());
            deadlineEntity.setTeacherId(classRoomEntity.getTeacherId());
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
    public GetDeadlinesResponse getDeadlinesByLessonId(String lessonId, Integer page, Integer size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<DeadlineEntity> deadlineEntities = deadlineRepository.findAllByLessonId(lessonId, pageable);
            List<GetDeadlinesResponse.DeadlineResponse> deadlineResponses = new ArrayList<>();
            for (DeadlineEntity deadlineEntity : deadlineEntities){
                GetDeadlinesResponse.DeadlineResponse deadlineResponse = GetDeadlinesResponse.DeadlineResponse.fromDeadlineEntity(deadlineEntity);
                deadlineResponses.add(deadlineResponse);
            }
            return GetDeadlinesResponse.builder()
                    .totalElements(deadlineEntities.getTotalElements())
                    .totalPage(deadlineEntities.getTotalPages())
                    .deadlines(deadlineResponses)
                    .build();

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
    public ClassroomDeadlineResponse getClassroomDeadlinesByClassroomId(String classroomId, Integer page, Integer size) {
        try {
            if (!classroomRepository.existsById(classroomId)) {
                throw new IllegalArgumentException("ClassroomId is not found");
            }

            int skip = page * size;
            List<ClassroomDeadlineResponse.DeadlineResponse> content = classroomRepository.getDeadlinesForClassroom(classroomId, skip, size);

            long totalElements = classroomRepository.countDeadlinesForClassroom(classroomId);
            int totalPages = (int) Math.ceil((double) totalElements / size);

            return ClassroomDeadlineResponse.builder()
                    .totalElements(totalElements)
                    .totalPage(totalPages)
                    .deadlines(mapDeadlineResponses(content))
                    .build();

        } catch (Exception e) {
            log.error("Error in getClassroomDeadlinesByClassroomId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetDeadlinesResponse getDeadlinesByTeacherId(String teacherId, String search, String status, String startDate, String endDate, Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page, size);


            // Xử lý chuỗi tìm kiếm
            String processedSearch = (search == null || search.trim().isEmpty()) ? ".*" : Pattern.quote(search.trim());

            Page<DeadlineEntity> deadlineEntities = deadlineRepository.findByTeacherIdWithFilters(
                    teacherId,
                    processedSearch,
                    status,
                    startDate,
                    endDate,
                    pageable);

            List<GetDeadlinesResponse.DeadlineResponse> deadlineResponses = deadlineEntities.getContent().stream()
                    .map(GetDeadlinesResponse.DeadlineResponse::fromDeadlineEntity)
                    .collect(Collectors.toList());

            return GetDeadlinesResponse.builder()
                    .totalElements(deadlineEntities.getTotalElements())
                    .totalPage(deadlineEntities.getTotalPages())
                    .deadlines(deadlineResponses)
                    .build();
        } catch (NumberFormatException e) {
            log.error("Error parsing date in getDeadlinesByTeacherId: ", e);
            throw new IllegalArgumentException("Invalid date format. Expected timestamp.");
        } catch (Exception e) {
            log.error("Error in getDeadlinesByTeacherId: ", e);
            throw new RuntimeException("Error retrieving deadlines: " + e.getMessage());
        }
    }
    private Document convertSortToDocument(Sort sort) {
        Document sortDoc = new Document();
        for (Sort.Order order : sort) {
            sortDoc.put(order.getProperty(), order.getDirection() == Sort.Direction.ASC ? 1 : -1);
        }
        return sortDoc;
    }
    @Override
    public GetDeadlinesResponse getDeadlinesByStudentId(
            String studentId, String search, String status, String startDate, String endDate,
            String classroomId, Integer page, Integer size, String sortBy, Sort.Direction sortDirection) {
        try {
            // Validate and sanitize sortBy
            List<String> allowedSortFields = Arrays.asList("startDate", "endDate", "title", "status");
            if (!allowedSortFields.contains(sortBy)) {
                sortBy = "startDate";  // Default to startDate if invalid field is provided
            }

            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Ensure parameters are not null
            search = (search == null) ? "" : search.trim();
            status = (status == null) ? "" : status.trim();
            startDate = (startDate == null) ? "" : startDate.trim();
            endDate = (endDate == null) ? "" : endDate.trim();
            classroomId = (classroomId == null) ? "" : classroomId.trim();

            Document sortDoc = convertSortToDocument(sort);
            Slice<GetDeadlinesResponse.DeadlineResponse> deadlineSlice = studentEnrollmentsRepository.getStudentDeadlines(
                    studentId, status, search, startDate, endDate, classroomId, sortDoc, pageable);

            List<GetDeadlinesResponse.DeadlineResponse> deadlineResponses = deadlineSlice.getContent().stream()
                    .map(this::convertToDeadlineResponse)
                    .collect(Collectors.toList());

            return GetDeadlinesResponse.builder()
                    .totalElements((long) deadlineSlice.getNumberOfElements())
                    .totalPage(deadlineSlice.getNumberOfElements() > 0 ? (deadlineSlice.getNumberOfElements() + size - 1) / size : 0)
                    .deadlines(deadlineResponses)
                    .build();
        } catch (Exception e) {
            log.error("Error in getDeadlinesByStudentId: ", e);
            throw new RuntimeException("Error retrieving deadlines: " + e.getMessage());
        }
    }
    private GetDeadlinesResponse.DeadlineResponse convertToDeadlineResponse(GetDeadlinesResponse.DeadlineResponse deadlineResponse) {
        return GetDeadlinesResponse.DeadlineResponse.builder()
                .id(deadlineResponse.getId())
                .title(deadlineResponse.getTitle())
                .description(deadlineResponse.getDescription())
                .type(deadlineResponse.getType())
                .status(deadlineResponse.getStatus())
                .startDate(deadlineResponse.getStartDate())
                .endDate(deadlineResponse.getEndDate())
                .classroomId(deadlineResponse.getClassroomId())
                .build();
    }

    private List<ClassroomDeadlineResponse.DeadlineResponse> mapDeadlineResponses(List<ClassroomDeadlineResponse.DeadlineResponse> content) {
        return content.stream()
                .map(this::mapDeadlineResponse)
                .collect(Collectors.toList());
    }

    private ClassroomDeadlineResponse.DeadlineResponse mapDeadlineResponse(ClassroomDeadlineResponse.DeadlineResponse source) {
        return ClassroomDeadlineResponse.DeadlineResponse.builder()
                .id(source.getId())
                .title(source.getTitle())
                .description(source.getDescription())
                .type(source.getType())
                .status(source.getStatus())
                .attachment(source.getAttachment())
                .startDate(source.getStartDate())
                .endDate(source.getEndDate())
                .lessonName(source.getLessonName())
                .lessonDescription(source.getLessonDescription())
                .sectionName(source.getSectionName())
                .sectionDescription(source.getSectionDescription())
                .classroomName(source.getClassroomName())
                .classroomDescription(source.getClassroomDescription())
                .build();
    }


}
