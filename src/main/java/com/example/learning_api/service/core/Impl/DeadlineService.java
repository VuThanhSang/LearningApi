package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;

import com.example.learning_api.dto.request.deadline.CreateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.GetUpcomingDeadlineResponse;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.deadline.*;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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
    private final FileRepository fileRepository;
    private final ScoringCriteriaRepository scoringCriteriaRepository;
    public void processFiles (List<MultipartFile> files,String title, DeadlineEntity deadlineEntity){
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            try {
                FAQEntity.SourceDto fileDto = processFile(file, title);
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(fileDto.getPath());
                fileEntity.setType(fileDto.getType().name());
                fileEntity.setOwnerType(FileOwnerType.DEADLINE);
                fileEntity.setOwnerId(deadlineEntity.getId());
                fileEntity.setExtension(fileDto.getPath().substring(fileDto.getPath().lastIndexOf(".") + 1));
                fileEntity.setName(title);
                fileEntity.setSize(String.valueOf(file.getSize()));
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error("Error processing file: ", e);
                throw new IllegalArgumentException("Error processing file: " + e.getMessage());
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
        }  else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + ".docx", fileBytes, "raw");
        }
        else {
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
    public void createDeadline(CreateDeadlineRequest body) {
        try {
            if (body.getLessonId()==null){
                throw new IllegalArgumentException("ClassroomId is required");
            }
            if (lessonRepository.findById(body.getLessonId()) == null){
                throw new IllegalArgumentException("lessonId is not found");
            }


            DeadlineEntity deadlineEntity = modelMapperService.mapClass(body, DeadlineEntity.class);

            LessonEntity lessonEntity = lessonRepository.findById(body.getLessonId()).orElse(null);

            SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).orElse(null);
            ClassRoomEntity classRoomEntity = classroomRepository.findById(sectionEntity.getClassRoomId()).orElse(null);
            deadlineEntity.setClassroomId(classRoomEntity.getId());
            deadlineEntity.setTeacherId(classRoomEntity.getTeacherId());
            deadlineEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            deadlineEntity.setStatus(DeadlineStatus.UPCOMING);
            deadlineRepository.save(deadlineEntity);

            processFiles(body.getFiles(),body.getTitle(),deadlineEntity);
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


            processFiles(body.getFiles(),body.getTitle(),deadlineEntity);
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
    public void createScoringCriteria(ScoringCriteriaEntity body) {
        try{
            DeadlineEntity deadlineEntity = deadlineRepository.findById(body.getDeadlineId()).orElse(null);
            if (deadlineEntity == null){
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            ScoringCriteriaEntity scoringCriteriaEntity = modelMapperService.mapClass(body, ScoringCriteriaEntity.class);
            scoringCriteriaEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            scoringCriteriaEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            scoringCriteriaRepository.save(scoringCriteriaEntity);
            deadlineEntity.setUseScoringCriteria(true);
            deadlineRepository.save(deadlineEntity);
        }
        catch (Exception e) {
            log.error("Error in createScoringCriteria: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateScoringCriteria(ScoringCriteriaEntity body, String scoringCriteriaId) {
        try{
            ScoringCriteriaEntity scoringCriteriaEntity = scoringCriteriaRepository.findById(scoringCriteriaId).orElse(null);
            if (scoringCriteriaEntity == null){
                throw new IllegalArgumentException("ScoringCriteriaId is not found");
            }
            if (body.getTitle()!=null){
                scoringCriteriaEntity.setTitle(body.getTitle());
            }
            if (body.getDescription()!=null){
                scoringCriteriaEntity.setDescription(body.getDescription());
            }
            if (body.getScore()!=null){
                scoringCriteriaEntity.setScore(body.getScore());
            }
            scoringCriteriaEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            scoringCriteriaRepository.save(scoringCriteriaEntity);
        }
        catch (Exception e) {
            log.error("Error in updateScoringCriteria: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteScoringCriteria(String scoringCriteriaId) {
        try{
            ScoringCriteriaEntity scoringCriteriaEntity = scoringCriteriaRepository.findById(scoringCriteriaId).orElse(null);
            if (scoringCriteriaEntity == null){
                throw new IllegalArgumentException("ScoringCriteriaId is not found");
            }
            scoringCriteriaRepository.deleteById(scoringCriteriaId);
            int count = scoringCriteriaRepository.countByDeadlineId(scoringCriteriaEntity.getDeadlineId());
            if (count == 0){
                DeadlineEntity deadlineEntity = deadlineRepository.findById(scoringCriteriaEntity.getDeadlineId()).orElse(null);
                if (deadlineEntity != null){
                    deadlineEntity.setUseScoringCriteria(false);
                    deadlineRepository.save(deadlineEntity);
                }
            }
        }
        catch (Exception e) {
            log.error("Error in deleteScoringCriteria: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public DeadlineResponse getDeadline(String deadlineId) {
        try{
            DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId).orElse(null);
            if (deadlineEntity == null){
                throw new IllegalArgumentException("DeadlineId is not found");
            }
            DeadlineResponse deadlineResponse = modelMapperService.mapClass(deadlineEntity, DeadlineResponse.class);
            List<FileEntity> fileEntities = fileRepository.findByOwnerIdAndOwnerType(deadlineId, FileOwnerType.DEADLINE.name());
            deadlineResponse.setFiles(fileEntities);
            if (deadlineEntity.getUseScoringCriteria()){
                List<ScoringCriteriaEntity> scoringCriteriaEntities = scoringCriteriaRepository.findByDeadlineId(deadlineId);
                deadlineResponse.setScoringCriteria(scoringCriteriaEntities);
            }
            return deadlineResponse;
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
                deadlineResponse.setUseScoringCriteria(deadlineEntity.getUseScoringCriteria());
                deadlineResponse.setFiles(fileRepository.findByOwnerIdAndOwnerType(deadlineEntity.getId(), FileOwnerType.DEADLINE.name()));
                if ( deadlineEntity.getUseScoringCriteria()!=null && deadlineEntity.getUseScoringCriteria()==true ){
                    List<ScoringCriteriaEntity> scoringCriteriaEntities = scoringCriteriaRepository.findByDeadlineId(deadlineEntity.getId());
                    deadlineResponse.setScoringCriteria(scoringCriteriaEntities);
                }
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

    public static long getStartOfDayTimestamp() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getStartOfWeekTimestamp() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getStartOfMonthTimestamp() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        return startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    @Override
    public GetUpcomingDeadlineResponse getUpcomingDeadlineByStudentId(String studentId,String filterType, Integer page, Integer size) {
        try{
            if (studentRepository.findById(studentId) == null){
                throw new IllegalArgumentException("StudentId is not found");
            }
            Pageable pageable = PageRequest.of(page, size);
            String date = String.valueOf(System.currentTimeMillis());
//            String date = "1724238234617";
            String startDate = "";
            String endDate = "";
            if(filterType==null){
                startDate = String.valueOf(System.currentTimeMillis());
                endDate = "9999999999999";
            }
            else if (filterType.equals("day")){
                startDate = String.valueOf(getStartOfDayTimestamp());
                endDate = String.valueOf(getStartOfDayTimestamp() + 86400000);
            }
            else if (filterType.equals("week")){
                startDate = String.valueOf(getStartOfWeekTimestamp());
                endDate = String.valueOf(getStartOfWeekTimestamp() + 604800000);
            }
            else if (filterType.equals("month")){
                startDate = String.valueOf(getStartOfMonthTimestamp());
                endDate = String.valueOf(getStartOfMonthTimestamp() + 2592000000L);
            }

            List<UpcomingDeadlinesResponse> deadlineEntities = studentEnrollmentsRepository.getUpcomingDeadlines(studentId, startDate, endDate, pageable);

            GetUpcomingDeadlineResponse getUpcomingDeadlineResponse = new GetUpcomingDeadlineResponse();
            getUpcomingDeadlineResponse.setUpcomingDeadlines(deadlineEntities);
            long totalElements = studentEnrollmentsRepository.countUpcomingDeadlines(studentId, startDate, endDate);
            getUpcomingDeadlineResponse.setTotalElements(totalElements);
            getUpcomingDeadlineResponse.setTotalPages((int) Math.ceil((double) totalElements / size));
            return getUpcomingDeadlineResponse;
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
            for (ClassroomDeadlineResponse.DeadlineResponse deadlineResponse : content) {
                List<FileEntity> files = fileRepository.findByOwnerIdAndOwnerType(deadlineResponse.getId(), FileOwnerType.DEADLINE.name());
                List<ScoringCriteriaEntity> scoringCriteriaEntities = scoringCriteriaRepository.findByDeadlineId(deadlineResponse.getId());
                deadlineResponse.setFiles(files);
                deadlineResponse.setScoringCriteria(scoringCriteriaEntities);
            }
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
                    .map(this::convertToDeadlineResponse)
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

    private GetDeadlinesResponse.DeadlineResponse convertToDeadlineResponse(DeadlineEntity deadlineEntity) {
        List<FileEntity> files = fileRepository.findByOwnerIdAndOwnerType(deadlineEntity.getId(), FileOwnerType.DEADLINE.name());
        return GetDeadlinesResponse.DeadlineResponse.builder()
                .id(deadlineEntity.getId())
                .lessonId(deadlineEntity.getLessonId())
                .title(deadlineEntity.getTitle())
                .description(deadlineEntity.getDescription())
                .type(deadlineEntity.getType())
                .status(deadlineEntity.getStatus())
                .files(files)
                .startDate(deadlineEntity.getStartDate())
                .endDate(deadlineEntity.getEndDate())
                .createdAt(deadlineEntity.getCreatedAt())
                .updatedAt(deadlineEntity.getUpdatedAt())
                .classroomId(deadlineEntity.getClassroomId())
                .build();
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
           Long totalElements = studentEnrollmentsRepository.countStudentDeadlines(
                    studentId, status, search, startDate, endDate, classroomId);

            int totalPages = (int) Math.ceil((double) totalElements / size);

            return GetDeadlinesResponse.builder()
                    .totalElements(totalElements)
                    .totalPage(totalPages)
                    .deadlines(deadlineResponses)
                    .build();
        } catch (Exception e) {
            log.error("Error in getDeadlinesByStudentId: ", e);
            throw new RuntimeException("Error retrieving deadlines: " + e.getMessage());
        }
    }

    public GetDeadlineStatistics getDeadlineStatistics(String classroomId, int page, int size) {
        try {
            ClassRoomEntity classroom = classroomRepository.findById(classroomId).orElse(null);
            if (classroom == null) {
                throw new IllegalArgumentException("Classroom not found");
            }

            long skip = (long) page * size;
           List<DeadlineStatistics> data = deadlineRepository.getDeadlineStatisticsByClassroomId(classroomId, skip, size);
           for (DeadlineStatistics deadlineStatistics : data) {
               List<DeadlineStatistics.StudentSubmission> students = deadlineStatistics.getStudents();
               for (DeadlineStatistics.StudentSubmission student : students) {
                   StudentEntity studentEntity = studentRepository.findById(student.getStudentId()).orElse(null);
                   if (studentEntity != null) {
                       student.setStudentName(studentEntity.getUser().getFullname());
                   }
               }
           }

            GetDeadlineStatistics response = new GetDeadlineStatistics();
            long total =  deadlineRepository.countDeadlinesByClassroomId(classroomId);
            response.setTotalElements(total);
            response.setTotalPage((int) Math.ceil((double) total / size));
            response.setDeadlineStatistics(data);
            return response;
        } catch (Exception e) {
            log.error("Error in getDeadlineStatistics: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private GetDeadlinesResponse.DeadlineResponse convertToDeadlineResponse(GetDeadlinesResponse.DeadlineResponse deadlineResponse) {
        List<FileEntity> files = fileRepository.findByOwnerIdAndOwnerType(deadlineResponse.getId(), FileOwnerType.DEADLINE.name());
        List<ScoringCriteriaEntity> scoringCriteriaEntities = scoringCriteriaRepository.findByDeadlineId(deadlineResponse.getId());
        return GetDeadlinesResponse.DeadlineResponse.builder()
                .id(deadlineResponse.getId())
                .title(deadlineResponse.getTitle())
                .description(deadlineResponse.getDescription())
                .type(deadlineResponse.getType())
                .files(files)
                .useScoringCriteria(deadlineResponse.getUseScoringCriteria())
                .scoringCriteria(scoringCriteriaEntities)
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
                .startDate(source.getStartDate())
                .files(source.getFiles())
                .scoringCriteria(source.getScoringCriteria())
                .useScoringCriteria(source.getUseScoringCriteria())
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
