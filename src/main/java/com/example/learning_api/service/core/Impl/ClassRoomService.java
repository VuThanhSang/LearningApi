package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.common.LessonCompleteDto;
import com.example.learning_api.dto.request.classroom.*;
import com.example.learning_api.dto.response.classroom.*;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.dto.response.test.TestResultOfTestResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.*;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ExcelReader;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.*;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomService implements IClassRoomService {
    private final ModelMapperService modelMapperService;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final ExcelReader excelReader;
    private final RecentClassRepository recentClassRepository;
    private final JoinClassRequestRepository joinClassRequestRepository;
    private final TestResultRepository testResultRepository;
    private final TestRepository testRepository;
    private final DeadlineRepository deadlineRepository;
    private final DeadlineSubmissionsRepository deadlineSubmissionsRepository;
    private final LessonRepository lessonRepository;
    private final NotificationService notificationService;
    private final ProgressRepository progressRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SectionService sectionService;
    private final ApprovalClassroomRepository approvalClassroomRepository;
    @Override
    public CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body) {
        try{

            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getTeacherId()==null){
                throw new IllegalArgumentException("TeacherId is required");
            }
            if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                throw new IllegalArgumentException("TeacherId is not found");
            }


            ClassRoomEntity classRoomEntity = modelMapperService.mapClass(body, ClassRoomEntity.class);
            classRoomEntity.setCurrentEnrollment(0);
            classRoomEntity.setCategoryId(body.getCategoryId());
            classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            CreateClassRoomResponse resData = new CreateClassRoomResponse();
            if (body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classRoomEntity.setImage(imageUploaded.getUrl());
            }
            classRoomEntity.setDuration(0L);
            classRoomEntity.setTotalExam(0);
            classRoomEntity.setTotalQuiz(0);
            classRoomEntity.setTotalAssignment(0);
            classRoomEntity.setTotalResource(0);
            classRoomEntity.setTotalStudent(0);
            classRoomEntity.setTotalLesson(0);
            classRoomEntity.setTotalVideo(0);
            classRoomEntity.setTotalDocument(0);
            classRoomRepository.save(classRoomEntity);

            classRoomEntity.setInviteCode(generateInviteCode(classRoomEntity.getId()));
            classRoomRepository.save(classRoomEntity);
            resData.setId(classRoomEntity.getId());
            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
            resData.setTeacherId(classRoomEntity.getTeacherId());
            resData.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
            resData.setStatus(classRoomEntity.getStatus().toString());
            resData.setTotalAssignment(classRoomEntity.getTotalAssignment());
            resData.setTotalExam(classRoomEntity.getTotalExam());
            resData.setTotalLesson(classRoomEntity.getTotalLesson());
            resData.setTotalQuiz(classRoomEntity.getTotalQuiz());
            resData.setTotalResource(classRoomEntity.getTotalResource());
            resData.setTotalStudent(classRoomEntity.getTotalStudent());
            resData.setCreatedAt(classRoomEntity.getCreatedAt().toString());
            resData.setUpdatedAt(classRoomEntity.getUpdatedAt().toString());
            if (body.getCategoryId()!=null){
                CategoryEntity categoryEntity = categoryRepository.findById(body.getCategoryId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                categoryEntity.setTotalClassRoom(categoryEntity.getTotalClassRoom()+1);
                categoryRepository.save(categoryEntity);
            }
            return resData;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String generateInviteCode(String id) {
        String encodedId = Base64.getEncoder().encodeToString(id.getBytes());
        return "CLASS-" + encodedId;
    }

    @Override
    public void updateClassRoom(UpdateClassRoomRequest body) {
        try {
            ClassRoomEntity classroom = classRoomRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if(body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classroom.setImage(imageUploaded.getUrl());
            }

            if (body.getName()!=null){
                classroom.setName(body.getName());
            }
            if (body.getDescription()!=null){
                classroom.setDescription(body.getDescription());
            }

            if (body.getTeacherId()!=null){
                if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                    throw new IllegalArgumentException("TeacherId is not found");
                }
                classroom.setTeacherId(body.getTeacherId());
            }

            if (body.getCategoryId()!=null){
                CategoryEntity category = categoryRepository.findById(classroom.getCategoryId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                category.setTotalClassRoom(category.getTotalClassRoom()-1);
                categoryRepository.save(category);
                CategoryEntity newCategory = categoryRepository.findById(body.getCategoryId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                newCategory.setTotalClassRoom(newCategory.getTotalClassRoom()+1);
                categoryRepository.save(newCategory);
                classroom.setCategoryId(body.getCategoryId());
            }


            if (body.getStatus()!=null){
                classroom.setStatus(body.getStatus());
            }

            if (body.getPrice()!=null){
                classroom.setPrice(body.getPrice());
            }
            if (body.getDuration()!=null){
                classroom.setDuration(body.getDuration().longValue());
            }
            classroom.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            classRoomRepository.save(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }


    @Override
    public void deleteClassRoom(String classroomId) {
        try {
             ClassRoomEntity classroom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoomRepository.delete(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomsResponse getClassRooms(int page, int size, String search, String studentId, String role, String status, String category) {
        try {
            // Remove leading comma if present
            if (search != null && search.startsWith(",")) {
                search = search.substring(1);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms;

            // Xác định điều kiện chung
            boolean isUser = "USER".equalsIgnoreCase(role);
            List<String> classRoomIds = isUser ?
                    studentEnrollmentsRepository.findByStudentId(studentId).stream()
                            .map(StudentEnrollmentsEntity::getClassroomId)
                            .collect(Collectors.toList()) : null;

            // Xây dựng truy vấn động dựa trên điều kiện
            if (isUser) {
                classRooms = fetchClassRoomsForUser(classRoomIds, search, status, category, pageable);
            } else {
                classRooms = fetchClassRoomsForTeacher(studentId, search, status, category, pageable);
            }

            // Map dữ liệu sang DTO
            List<GetClassRoomsResponse.ClassRoomResponse> resData = classRooms.stream().map(classRoom -> {
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                if (classRoom.getCategoryId() != null) {
                    classRoomResponse.setCategoryName(categoryRepository.findById(classRoom.getCategoryId())
                            .orElse(null).getName());
                }

                TeacherEntity teacher = teacherRepository.findById(classRoom.getTeacherId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                UserEntity user = userRepository.findById(teacher.getUserId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                teacher.setUser(null);
                user.setTeacher(teacher);
                classRoomResponse.setUser(user);
                int count = studentEnrollmentsRepository.countByClassroomId(classRoom.getId());
                classRoomResponse.setCurrentEnrollment(count);
                classRoomResponse.setCategoryId(classRoom.getCategoryId());
                List<ReviewEntity> allReviews = reviewRepository.findByClassroomId(classRoomResponse.getId());
                double averageRating = allReviews.stream()
                        .mapToDouble(ReviewEntity::getRating)
                        .average()
                        .orElse(0.0);
                classRoomResponse.setRating(averageRating);
                classRoomResponse.setTotalRating(allReviews.size());
                List<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classRoomResponse.getId());

                List<String> lessonIds = new ArrayList<>();
                for (SectionEntity sectionEntity : sectionEntities) {
                    List<LessonEntity> lessonEntities = lessonRepository.findBySectionId(sectionEntity.getId());
                    for (LessonEntity lessonEntity : lessonEntities) {
                        lessonIds.add(lessonEntity.getId());
                    }
                }
                List<ProgressEntity> countComplete = progressRepository.findByClassroomIdAndLessonIdInAndCompletedAndStudentId(
                        classRoomResponse.getId(), lessonIds, true, studentId
                );
                classRoomResponse.setTotalLessonComplete(countComplete.size());
                return classRoomResponse;
            }).collect(Collectors.toList());

            // Đóng gói kết quả
            return new GetClassRoomsResponse(resData, classRooms.getTotalPages(), classRooms.getTotalElements());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Page<ClassRoomEntity> fetchClassRoomsForUser(List<String> classRoomIds, String search, String status, String category, Pageable pageable) {
        status = ClassRoomStatus.COMPLETED.toString();
        if (status != null) {
            if (category != null && !category.isEmpty()) {
                return classRoomRepository.findByCategoryAndNameContainingAndStatus(classRoomIds, category, search, status, pageable);
            } else {
                return classRoomRepository.findByIdInAndNameContainingAndStatus(classRoomIds, search, status, pageable);
            }
        } else {
            if (category != null && !category.isEmpty()) {
                return classRoomRepository.findByCategoryAndNameContaining(classRoomIds, category, search, pageable);
            } else {
                return classRoomRepository.findByIdInAndNameContaining(classRoomIds, search, pageable);
            }
        }
    }

    private Page<ClassRoomEntity> fetchClassRoomsForTeacher(String teacherId, String search, String status, String category, Pageable pageable) {
        if (status != null) {
            if (category != null && !category.isEmpty()) {
                return classRoomRepository.findByTeacherIDCategoryAndNameContainingAndStatus(teacherId, category, search, status, pageable);
            } else {
                return classRoomRepository.findByTeacherIdAndNameContainingAndStatus(teacherId, search, status, pageable);
            }
        } else {
            if (category != null && !category.isEmpty()) {
                return classRoomRepository.findByTeacherIDCategoryAndNameContaining(teacherId, category, search, pageable);
            } else {
                return classRoomRepository.findByTeacherIdAndNameContaining(teacherId, search, pageable);
            }
        }
    }



    @Override
    public GetClassRoomsResponse getUnregisteredClassRooms(int page, int size, String search, String studentId, String status, String category, String tag, String order) {
        try {
            // Remove leading comma if present
            if (search != null && search.startsWith(",")) {
                search = search.substring(1);
            }
            Sort sort = Sort.unsorted();
            if (tag.equals("price") && order!=null){
                if ("asc".equalsIgnoreCase(order)) {
                    sort = Sort.by(Sort.Order.asc("price"));
                } else {
                    sort = Sort.by(Sort.Order.desc("price"));
                }
            }
            Pageable pageable = PageRequest.of(page, size,sort);

            // Lấy danh sách lớp học mà sinh viên đã đăng ký
            List<String> registeredClassRoomIds = studentEnrollmentsRepository.findByStudentId(studentId).stream()
                    .map(StudentEnrollmentsEntity::getClassroomId)
                    .collect(Collectors.toList());

            // Tìm lớp học chưa đăng ký
            Page<ClassRoomEntity> unregisteredClassRooms = fetchUnregisteredClassRooms(
                    registeredClassRoomIds, search, status, category, tag, pageable, order
            );

            // Map dữ liệu sang DTO
            List<GetClassRoomsResponse.ClassRoomResponse> resData = unregisteredClassRooms.stream().map(classRoom -> {
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                if (classRoom.getCategoryId() != null) {
                    classRoomResponse.setCategoryName(categoryRepository.findById(classRoom.getCategoryId())
                            .orElse(null).getName());
                }
                int count = studentEnrollmentsRepository.countByClassroomId(classRoom.getId());
                classRoomResponse.setCurrentEnrollment(count);
                TeacherEntity teacher = teacherRepository.findById(classRoom.getTeacherId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                UserEntity user = userRepository.findById(teacher.getUserId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                teacher.setUser(null);
                user.setTeacher(teacher);
                classRoomResponse.setUser(user);
                classRoomResponse.setCategoryId(classRoom.getCategoryId());
                List<ReviewEntity> allReviews = reviewRepository.findByClassroomId(classRoomResponse.getId());
                double averageRating = allReviews.stream()
                        .mapToDouble(ReviewEntity::getRating)
                        .average()
                        .orElse(0.0);
                classRoomResponse.setRating(averageRating);
                classRoomResponse.setTotalRating(allReviews.size());
                return classRoomResponse;
            }).collect(Collectors.toList());

            // Đóng gói kết quả
            return new GetClassRoomsResponse(resData, unregisteredClassRooms.getTotalPages(), unregisteredClassRooms.getTotalElements());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    private Page<ClassRoomEntity> fetchUnregisteredClassRooms(
            List<String> registeredClassRoomIds,
            String search,
            String status,
            String category,
            String tag,
            Pageable pageable,
            String order
    ) {
        // Set status to COMPLETED
        status = ClassRoomStatus.COMPLETED.toString();

        // Xử lý tag
        if (tag != null && !tag.isEmpty()) {
            if (category != null && !category.isEmpty()) {
                if ("popular".equalsIgnoreCase(tag)) {
                    List<ClassRoomEntity> popularClassrooms = classRoomRepository.findPopularClassroomsByCategory(
                            registeredClassRoomIds, category, search, status);
                    return createPageFromList(popularClassrooms, pageable);
                } else if ("new".equalsIgnoreCase(tag)) {
                    List<ClassRoomEntity> newClassrooms = classRoomRepository.findNewClassroomsByCategory(
                            registeredClassRoomIds, category, search, pageable,status);
                    return createPageFromList(newClassrooms, pageable);
                } else if ("price".equalsIgnoreCase(tag)) {
                    boolean ascending = "asc".equalsIgnoreCase(order);
                    List<ClassRoomEntity> priceSortedClassrooms = classRoomRepository.findByCategoryAndSortByPrice(
                            category, registeredClassRoomIds, search, pageable, ascending
                    );
                    return createPageFromList(priceSortedClassrooms, pageable);
                } else {
                    int sampleSize = pageable.getPageSize() * (pageable.getPageNumber() + 1);
                    List<ClassRoomEntity> randomClassrooms = classRoomRepository.findRandomClassroomsByCategory(
                            registeredClassRoomIds, category, search, sampleSize);
                    return createPageFromList(randomClassrooms, pageable);
                }
            }
            if ("popular".equalsIgnoreCase(tag)) {
                List<ClassRoomEntity> popularClassrooms = classRoomRepository.findPopularClassrooms(registeredClassRoomIds, search, status);
                return createPageFromList(popularClassrooms, pageable);
            } else if ("new".equalsIgnoreCase(tag)) {
                List<ClassRoomEntity> newClassrooms = classRoomRepository.findNewClassrooms(registeredClassRoomIds, search, pageable);
                return createPageFromList(newClassrooms, pageable);
            } else if ("price".equalsIgnoreCase(tag)) {
                // Fetch classrooms sorted by price
                List<ClassRoomEntity> priceSortedClassrooms = classRoomRepository.findByAndSortByPrice(
                        registeredClassRoomIds, search, pageable
                );
                return createPageFromList(priceSortedClassrooms, pageable);
            } else {
                int sampleSize = pageable.getPageSize() * (pageable.getPageNumber() + 1);
                List<ClassRoomEntity> randomClassrooms = classRoomRepository.findRandomClassrooms(
                        registeredClassRoomIds, search, sampleSize);
                return createPageFromList(randomClassrooms, pageable);
            }
        }

        // Xử lý kết hợp tag với category
        if (category != null && !category.isEmpty()) {
            if (status != null) {
                return classRoomRepository.findByCategoryAndNameContainingAndStatusNotIn(
                        registeredClassRoomIds, category, search, status, pageable
                );
            } else {
                return classRoomRepository.findByCategoryAndNameContainingAndIdNotIn(
                        category, search, registeredClassRoomIds, pageable
                );
            }
        } else {
            if (status != null) {
                return classRoomRepository.findByIdNotInAndNameContainingAndStatus(
                        registeredClassRoomIds, search, status, pageable
                );
            } else {
                return classRoomRepository.findByIdNotInAndNameContaining(
                        registeredClassRoomIds, search, pageable
                );
            }
        }
    }

    private Page<ClassRoomEntity> createPageFromList(List<ClassRoomEntity> data, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), data.size());
        List<ClassRoomEntity> subList = data.subList(start, end);
        return new PageImpl<>(subList, pageable, data.size());
    }

    @Override
    public GetClassRoomsResponse getClassRoomsByTeacherId(int page, int size, String teacherId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms = classRoomRepository.findByTeacherId(teacherId, pageAble);
            List<GetClassRoomsResponse.ClassRoomResponse> resData = new ArrayList<>();
            for (ClassRoomEntity classRoom : classRooms){
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                if (classRoom.getCategoryId() != null) {
                    classRoomResponse.setCategoryName(categoryRepository.findById(classRoom.getCategoryId())
                            .orElse(null).getName());
                }
                resData.add(classRoomResponse);
            }
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage(classRooms.getTotalPages());
            res.setTotalElements(classRooms.getTotalElements());
            return res;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }



    @Override
    public GetSectionsResponse getSectionsByClassroomId(int page, int size, String classroomId, String role) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            List<String> status = new ArrayList<>();
            status.add("PUBLIC");
            if (role.equals("TEACHER")){
                status.add("PRIVATE");
            }
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId, pageAble,status);
            List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
            GetSectionsResponse resData = new GetSectionsResponse();
            resData.setTotalPage(sectionEntities.getTotalPages());
            resData.setTotalElements(sectionEntities.getTotalElements());
            resData.setSections(sectionResponses);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }



    @Override
    public GetClassRoomDetailResponse getClassRoomByInvitationCode(String invitationCode) {
        try{
            ClassRoomEntity classRoomEntity = classRoomRepository.findClassRoomEntityByInviteCode(invitationCode);
            if (classRoomEntity==null){
                throw new IllegalArgumentException("ClassRoom is not found");
            }
            List<String> status =new ArrayList<>();
            status.add("PUBLIC");
            GetClassRoomDetailResponse resData = new GetClassRoomDetailResponse();
            Pageable pageAble = PageRequest.of(0, 15);
            resData.setId(classRoomEntity.getId());
            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
            resData.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
            resData.setInviteCode(classRoomEntity.getInviteCode());
            resData.setCategoryId(classRoomEntity.getCategoryId());
            if (classRoomEntity.getCategoryId() != null) {
                resData.setCategoryName(categoryRepository.findById(classRoomEntity.getCategoryId())
                        .orElse(null).getName());
            }
            resData.setStatus(classRoomEntity.getStatus());
            resData.setTeacherId(classRoomEntity.getTeacherId());
            resData.setCreatedAt(classRoomEntity.getCreatedAt());
            resData.setUpdatedAt(classRoomEntity.getUpdatedAt());
            TeacherEntity teacher = teacherRepository.findById(resData.getTeacherId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            UserEntity user = userRepository.findById(teacher.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            teacher.setUser(null);
            user.setTeacher(teacher);
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classRoomEntity.getId(),pageAble,status);
            List<GetClassRoomDetailResponse.Section> sections = new ArrayList<>();
            for (SectionEntity sectionEntity : sectionEntities){
                GetClassRoomDetailResponse.Section section = new GetClassRoomDetailResponse.Section();
                section.setId(sectionEntity.getId());
                section.setName(sectionEntity.getName());
                section.setStatus(sectionEntity.getStatus()!=null?sectionEntity.getStatus().toString():null);
                section.setDescription(sectionEntity.getDescription());
                section.setIndex(sectionEntity.getIndex()!=null?sectionEntity.getIndex():0);
//                List<LessonEntity> lessons = lessonRepository.findBySectionId(sectionEntity.getId());
//                List<GetLessonDetailResponse> lessonDetails = new ArrayList<>();
//                for (LessonEntity lesson : lessons){
//                    GetLessonDetailResponse lessonDetail = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lesson.getId());
//                    lessonDetails.add(lessonDetail);
//                }
//                section.setLessons(lessonDetails);
                sections.add(section);

            }
            resData.setSections(sections);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomDetailResponse getClassRoomDetail(String classroomId,String role,String userId) {
       try{
              ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId)
                      .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
              List<String> status =new ArrayList<>();
              status.add("PUBLIC");
              if (role.equals("TEACHER")){
                  status.add("PRIVATE");
              }

                GetClassRoomDetailResponse resData = new GetClassRoomDetailResponse();
                Pageable pageAble = PageRequest.of(0, 15);
                resData = modelMapperService.mapClass(classRoomEntity, GetClassRoomDetailResponse.class);
                resData.setCategoryId(classRoomEntity.getCategoryId());
                if (classRoomEntity.getCategoryId() != null) {
                    resData.setCategoryName(categoryRepository.findById(classRoomEntity.getCategoryId())
                            .orElse(null).getName());
                }

                //                resData.setId(classRoomEntity.getId());
//                resData.setName(classRoomEntity.getName());
//                resData.setDescription(classRoomEntity.getDescription());
//                resData.setImage(classRoomEntity.getImage());
//                resData.setEnrollmentCapacity(classRoomEntity.getEnrollmentCapacity());
//                resData.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
//                resData.setInviteCode(classRoomEntity.getInviteCode());
//                resData.setStatus(classRoomEntity.getStatus());
//                resData.setTeacherId(classRoomEntity.getTeacherId());
//                resData.setCreatedAt(classRoomEntity.getCreatedAt());
//                resData.setUpdatedAt(classRoomEntity.getUpdatedAt());
           TeacherEntity teacher = teacherRepository.findById(resData.getTeacherId())
                   .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
           UserEntity user = userRepository.findById(teacher.getUserId())
                   .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
           teacher.setUser(null);
           user.setTeacher(teacher);
                StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId(userId, classroomId);
                resData.setEnrolled(studentEnrollmentsEntity != null);
                Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId,pageAble,status);
                List<GetClassRoomDetailResponse.Section> sections = new ArrayList<>();
                for (SectionEntity sectionEntity : sectionEntities){
                    GetClassRoomDetailResponse.Section section = new GetClassRoomDetailResponse.Section();
                    section.setId(sectionEntity.getId());
                    section.setName(sectionEntity.getName());
                    section.setStatus(sectionEntity.getStatus() != null ? sectionEntity.getStatus().toString() : null);
                    section.setDescription(sectionEntity.getDescription());
                    section.setIndex(sectionEntity.getIndex()!=null?sectionEntity.getIndex():0);
                    SectionEntity previousSection = sectionRepository.findByClassRoomIdAndIndex(classroomId, section.getIndex() - 1);
                    if (section.getIndex()==0||role.equals("TEACHER")){
                        section.setCanAccess(true);
                    }else if (previousSection == null) {
                        section.setCanAccess(false);
                    }else{
                        section.setCanAccess(progressRepository.existsByStudentIdAndClassroomIdAndSectionIdAndCompleted(userId, classroomId, previousSection.getId(), true));
                    }
                    section.setIsComplete(progressRepository.existsByStudentIdAndClassroomIdAndSectionIdAndCompleted(userId, classroomId, sectionEntity.getId(), true));
                    sections.add(section);

                }
                resData.setSections(sections);

           List<String> lessonIds = new ArrayList<>();
           for (SectionEntity sectionEntity : sectionEntities) {
               List<LessonEntity> lessonEntities = lessonRepository.findBySectionId(sectionEntity.getId());
               for (LessonEntity lessonEntity : lessonEntities) {
                   lessonIds.add(lessonEntity.getId());
               }
           }
           List<ProgressEntity> countComplete = progressRepository.findByClassroomIdAndLessonIdInAndCompletedAndStudentId(
                   resData.getId(), lessonIds, true, userId
           );
           resData.setTotalLessonComplete(countComplete.size());
                return resData;

       }
         catch (Exception e){
              throw new IllegalArgumentException(e.getMessage());
         }
    }

    @Override
    public void importClassRoom(ImportClassRoomRequest body) {
        try{
            if (body.getFile()==null){
                throw new IllegalArgumentException(" File is required");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);
                ClassRoomEntity classRoomEntity = new ClassRoomEntity();
                classRoomEntity.setName(row.get(0));
                classRoomEntity.setDescription(row.get(1));

                classRoomEntity.setTeacherId(row.get(4));

                classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomRepository.save(classRoomEntity);
            }
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    @Override
    public GetClassRoomRecentResponse getRecentClassesByTeacherId(int page, int size, String userId,String role) {
        try {
            int skip = page * size;
            List<RecentClassDTO> classRooms = new ArrayList<>();
            long totalElements = 0;
            if (role.equals("USER")){
                classRooms = recentClassRepository.findRecentClassesByStudentId(userId, skip, size);
                totalElements = recentClassRepository.countRecentClassesByStudentId(userId);
            }
            else if (role.equals("TEACHER")){
                classRooms = recentClassRepository.findRecentClassesByTeacherId(userId, skip, size);
                totalElements = recentClassRepository.countRecentClassesByTeacherId(userId);

            }

            int totalPages = (int) Math.ceil((double) totalElements / size);

            GetClassRoomRecentResponse resData = new GetClassRoomRecentResponse();
            resData.setTotalElements((int) totalElements);
            resData.setTotalPage(totalPages);
            List<GetClassRoomRecentResponse.ClassRoomResponse> data = new ArrayList<>();
            for (RecentClassDTO classRoom : classRooms) {
                GetClassRoomRecentResponse.ClassRoomResponse resClassRoom = new GetClassRoomRecentResponse.ClassRoomResponse();
                resClassRoom.setClassRoom(classRoomRepository.findById(classRoom.getClassId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND)));
                resClassRoom.setLastAccessedAt(classRoom.getLastAccessedAt());
                int count = studentEnrollmentsRepository.countByClassroomId(classRoom.getClassId());
                resClassRoom.getClassRoom().setCurrentEnrollment(count);
                data.add(resClassRoom);
            }
            resData.setData(data);
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void joinClassRoom(String classroomId, String studentId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (classRoomRepository.findById(classroomId).isEmpty()) {
                throw new IllegalArgumentException("ClassroomId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId(classroomId, studentId);
            if (studentEnrollmentsEntity != null) {
                throw new IllegalArgumentException("Student is already enrolled in this class");
            }
            JoinClassRequestEntity joinClassRequestEntity = new JoinClassRequestEntity();
            joinClassRequestEntity.setClassroomId(classroomId);
            joinClassRequestEntity.setStudentId(studentId);
            joinClassRequestEntity.setStatus(JoinRequestStatus.PENDING);
            joinClassRequestEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinClassRequestEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetJoinClassResponse getJoinClassRequests(int page, int size, String classroomId, String teacherId, String email, String name) {
        try {
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if (!classRoom.getTeacherId().equals(teacherId)) {
                throw new IllegalArgumentException("TeacherId is not authorized to view join requests");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<JoinClassRequestEntity> joinRequests = joinClassRequestRepository.findByClassroomId(classroomId, pageable);

            List<GetJoinClassResponse.JoinRequest> data = new ArrayList<>();
            long totalElements = 0;
            for (JoinClassRequestEntity joinRequest : joinRequests) {
                StudentEntity student = studentRepository.findById(joinRequest.getStudentId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                if ((email == null || student.getUser().getEmail().matches(".*" + email + ".*")) &&
                        (name == null || student.getUser().getFullname().matches(".*" + name + ".*"))) {
                    GetJoinClassResponse.JoinRequest resJoinRequest = new GetJoinClassResponse.JoinRequest();
                    resJoinRequest.setId(joinRequest.getId());
                    resJoinRequest.setStudent(student);
                    resJoinRequest.setClassroomId(joinRequest.getClassroomId());
                    resJoinRequest.setStatus(joinRequest.getStatus().toString());
                    resJoinRequest.setCreatedAt(joinRequest.getCreatedAt());
                    resJoinRequest.setUpdatedAt(joinRequest.getUpdatedAt());
                    data.add(resJoinRequest);
                    totalElements++;
                }
            }
            GetJoinClassResponse res = new GetJoinClassResponse();
            res.setJoinRequests(data);
            res.setTotalPage((int) Math.ceil((double) totalElements / size));
            res.setTotalElements(totalElements);
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void acceptJoinClass(String classroomId, String studentId) {
        try {
            JoinClassRequestEntity joinRequest = joinClassRequestRepository.findByClassroomIdAndStudentId(classroomId, studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            joinRequest.setStatus(JoinRequestStatus.APPROVED);
            joinRequest.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinRequest);
            StudentEnrollmentsEntity checkEnroll = studentEnrollmentsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            if (checkEnroll != null) {
                throw new IllegalArgumentException("Student is already enrolled in this class");
            }
            StudentEnrollmentsEntity student = new StudentEnrollmentsEntity();
            student.setClassroomId(classroomId);
            student.setStudentId(studentId);
            student.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            student.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
            student.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId

            ).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void rejectJoinClass(String classroomId, String studentId) {
        try {
            JoinClassRequestEntity joinRequest = joinClassRequestRepository.findByClassroomIdAndStudentId(classroomId, studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            joinRequest.setStatus(JoinRequestStatus.REJECTED);
            joinRequest.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            joinClassRequestRepository.save(joinRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void removeStudentFromClass(String classroomId, String studentId, String teacherId) {
        try{
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if (!classRoom.getTeacherId().equals(teacherId)) {
                throw new IllegalArgumentException("TeacherId is not authorized to remove students");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            if (studentEnrollmentsEntity == null) {
                throw new IllegalArgumentException("Student is not enrolled in this class");
            }
            studentEnrollmentsRepository.delete(studentEnrollmentsEntity);
            if (classRoom.getCurrentEnrollment() - 1>=0)
                classRoom.setCurrentEnrollment(classRoom.getCurrentEnrollment() - 1);
            classRoomRepository.save(classRoom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public InviteClassByEmailResponse inviteStudentByEmail(InviteStudentByEmailRequest body) {
        try{
            ClassRoomEntity classRoom = classRoomRepository.findById(body.getClassroomId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if (!classRoom.getTeacherId().equals(body.getTeacherId())) {
                throw new IllegalArgumentException("TeacherId is not authorized to invite students");
            }
            List<String> errors = new ArrayList<>();
            List<String> success = new ArrayList<>();
            if(body.getInviteType().equals("FILE")){
                if (body.getFile()==null){
                    throw new IllegalArgumentException("File is required");
                }
                List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
                for (int i = 0; i < data.size(); i++) {
                    List<String> row = data.get(i);
                    StudentEntity studentUser = studentRepository.findByEmail(row.get(0));
                    if (studentUser == null) {
                        errors.add("Student with email " + row.get(0) + " not found");
                        continue;
                    }
                    StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId( studentUser.getId(),classRoom.getId());
                    if (studentEnrollmentsEntity != null) {
                        errors.add("Student with email " + row.get(0) + " is already enrolled in this class");
                        continue;
                    }
                    StudentEnrollmentsEntity newData = new StudentEnrollmentsEntity();
                    newData.setStudentId(studentUser.getId());
                    newData.setClassroomId(classRoom.getId());
                    newData.setGrade("0");
                    newData.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
                    newData.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
                    newData.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    newData.setUpdatedAt(String.valueOf(System.currentTimeMillis()));

                    success.add(row.get(0));
                }
            }else{

                for (String email : body.getEmails()) {
                    StudentEntity studentUser = studentRepository.findByEmail(email);
                    if (studentUser == null) {
                        throw new IllegalArgumentException("Student with email " + email + " not found");
                    }
                    StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndClassroomId( studentUser.getId(),classRoom.getId());
                    if (studentEnrollmentsEntity != null) {
                        errors.add("Student with email " + email + " is already enrolled in this class");
                        continue;
                    }
                    StudentEnrollmentsEntity newData = new StudentEnrollmentsEntity();
                    newData.setStudentId(studentUser.getId());
                    newData.setClassroomId(classRoom.getId());
                    newData.setGrade("0");
                    newData.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
                    newData.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
                    newData.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                    newData.setUpdatedAt(String.valueOf(System.currentTimeMillis()));

                    success.add(email);
                }
            }
            InviteClassByEmailResponse res = new InviteClassByEmailResponse();
            res.setSuccess(success);
            res.setFail(errors);
            return res;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetDetailStudentInClassResponse getDetailStudentInClass(String classroomId, String studentId) {
        try {
            ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            StudentEntity student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            StudentEnrollmentsEntity studentEnrollment = studentEnrollmentsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            if (studentEnrollment == null) {
                throw new IllegalArgumentException("Student is not enrolled in this class");
            }
            GetDetailStudentInClassResponse res = modelMapperService.mapClass(student, GetDetailStudentInClassResponse.class);
            List<DeadlineEntity> listDeadlineOfClass = deadlineRepository.findAllByClassroomId(classroomId);
            List<TestResultOfTestResponse> listTestResult = testResultRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            List<DeadlineSubmissionsEntity> listDeadlineSubmission = deadlineSubmissionsRepository.findByStudentIdAndClassroomId(studentId, classroomId);
            Pageable pageable = PageRequest.of(0, 99);
            Page<TestEntity> listTestOfClass = testRepository.findByClassroomId(classroomId, pageable);
            List<TestEntity> listTest = listTestOfClass.getContent();

            // Fetch all test results
            // Fetch all test results
            List<TestResultEntity> allTestResults = listTest.stream()
                    .map(test -> listTestResult.stream()
                            .filter(result -> result.getTestId().equals(test.getId()))
                            .max(Comparator.comparingDouble(TestResultOfTestResponse::getGrade))
                            .map(result -> {
                                TestResultEntity entity = new TestResultEntity();
                                entity.setId(result.getResultId());
                                entity.setTestId(result.getTestId());
                                entity.setStudentId(result.getStudentId());
                                entity.setGrade(result.getGrade());
                                entity.setIsPassed(result.getIsPassed());
                                entity.setAttendedAt(result.getAttendedAt());
                                entity.setFinishedAt(result.getFinishedAt());
                                entity.setState(TestState.valueOf(result.getState()));
                                return entity;
                            })
                            .orElseGet(() -> {
                                TestResultEntity notAttempted = new TestResultEntity();
                                notAttempted.setTestId(test.getId());
                                notAttempted.setStudentId(studentId);
                                notAttempted.setState(TestState.NOT_STARTED);
                                return notAttempted;
                            }))
                    .collect(Collectors.toList());
            // Fetch all deadline submissions
            List<DeadlineSubmissionsEntity> allDeadlineSubmissions = listDeadlineOfClass.stream()
                    .map(deadline -> listDeadlineSubmission.stream()
                            .filter(submission -> submission.getDeadlineId().equals(deadline.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                DeadlineSubmissionsEntity notAttempted = new DeadlineSubmissionsEntity();
                                notAttempted.setDeadlineId(deadline.getId());
                                notAttempted.setStudentId(studentId);
                                notAttempted.setStatus(DeadlineSubmissionStatus.NOT_SUBMITTED);
                                return notAttempted;
                            }))
                    .collect(Collectors.toList());
            List<TestEntity> tests = testRepository.findByClassroomId(classroomId);
            List<DeadlineEntity> deadlines = deadlineRepository.findAllByClassroomId(classroomId);
            for (TestEntity test : tests) {
                List<TestResultEntity> testResults = allTestResults.stream()
                        .filter(result -> result.getTestId().equals(test.getId()))
                        .toList();
                if (testResults.isEmpty()) {
                    TestResultEntity notAttempted = new TestResultEntity();
                    notAttempted.setTestId(test.getId());
                    notAttempted.setStudentId(studentId);
                    notAttempted.setState(TestState.NOT_STARTED);
                    allTestResults.add(notAttempted);

                }
                test.setTestResults(testResults);

            }
            for (DeadlineEntity deadline : deadlines) {
                List<DeadlineSubmissionsEntity> deadlineSubmissions = allDeadlineSubmissions.stream()
                        .filter(submission -> submission.getDeadlineId().equals(deadline.getId()))
                        .toList();
                if (deadlineSubmissions.isEmpty()) {
                    DeadlineSubmissionsEntity notAttempted = new DeadlineSubmissionsEntity();
                    notAttempted.setDeadlineId(deadline.getId());
                    notAttempted.setStudentId(studentId);
                    notAttempted.setStatus(DeadlineSubmissionStatus.NOT_SUBMITTED);
                    allDeadlineSubmissions.add(notAttempted);
                }
                deadline.setSubmissions(deadlineSubmissions);
            }
            res.setStudentAvatar(student.getUser().getAvatar());
            res.setStudentName(student.getUser().getFullname());
            res.setTests(tests);
            res.setDeadlines(deadlines);
            res.setTotalTest(tests.size());
            res.setTotalTestTaken((int) allTestResults.stream().filter(result -> result.getState() != TestState.NOT_STARTED).count());
            GetSectionsResponse sections = sectionService.getSectionsByClassRoomId(classroomId,0,99,"USER",studentId);
            int totalLessonCompleted = 0;
            int totalLesson= 0;
            for (GetSectionsResponse.SectionResponse section : sections.getSections()){
                for (GetSectionsResponse.LessonResponse lesson : section.getLessons()){
                    totalLesson++;
                    if (lesson.getIsComplete()){
                        totalLessonCompleted++;
                    }
                }
            }
            res.setTotalLesson(totalLesson);
            res.setTotalLessonComplete(totalLessonCompleted);
            res.setProgress((double) Math.ceil((double) totalLessonCompleted / res.getTotalLesson() * 100));
            Pageable pageable1 = PageRequest.of(0, 3);
            Page<ProgressEntity> progresses = progressRepository.findByStudentIdAndClassroomId(studentId, classroomId, pageable1);
            List<LessonCompleteDto> lessonCompletes = new ArrayList<>();
            for (ProgressEntity progress : progresses) {
                LessonCompleteDto lessonComplete = new LessonCompleteDto();
                lessonComplete.setLessonId(progress.getLessonId());
                LessonEntity lesson = lessonRepository.findById(progress.getLessonId())
                        .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                lessonComplete.setLessonName(lesson.getName());
                lessonComplete.setCreatedAt(progress.getCompletedAt());
                lessonCompletes.add(lessonComplete);
            }
            res.setLastLessonComplete(lessonCompletes);
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetClassRoomForAdminResponse getClassRoomsForAdmin(String classroomId) {
        try{
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            List<String> status =new ArrayList<>();
            status.add("PUBLIC");
            status.add("PRIVATE");

            GetClassRoomForAdminResponse resData = new GetClassRoomForAdminResponse();
            Pageable pageAble = PageRequest.of(0, 15);
            resData.setClassRoom(classRoomEntity);
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId,pageAble,status);
            List<GetClassRoomForAdminResponse.Section> sections = new ArrayList<>();
            for (SectionEntity sectionEntity : sectionEntities){
                GetClassRoomForAdminResponse.Section section =new  GetClassRoomForAdminResponse.Section();
                section.setId(sectionEntity.getId());
                section.setName(sectionEntity.getName());
                section.setStatus(sectionEntity.getStatus() != null ? sectionEntity.getStatus().toString() : null);
                section.setDescription(sectionEntity.getDescription());
                section.setIndex(sectionEntity.getIndex()!=null?sectionEntity.getIndex():0);
                    List<LessonEntity> lessons = lessonRepository.findBySectionId(sectionEntity.getId(),Sort.by(Sort.Direction.ASC,"index"),status);
                    List<GetLessonDetailResponse> lessonDetails = new ArrayList<>();
                    for (LessonEntity lesson : lessons){
                        GetLessonDetailResponse lessonDetail = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lesson.getId());
                        lessonDetails.add(lessonDetail);
                    }
                    section.setLessons(lessonDetails);
                sections.add(section);

            }
            List<TestEntity> tests = testRepository.findByClassroomId(classroomId);
            List<StudentEntity> students = studentEnrollmentsRepository.findByClassroomId(classroomId).stream()
                    .map(studentEnrollmentsEntity -> studentRepository.findById(studentEnrollmentsEntity.getStudentId()).get())
                    .toList();
            List<UserEntity> users = new ArrayList<>();
            for (StudentEntity student : students){
                UserEntity user = student.getUser();
                student.setUser(null);
                user.setPassword(null);
                users.add(user);
            }
            resData.setSections(sections);
            resData.setTests(tests);
            resData.setStudents(users);
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void changeStatusClassRoom(String classroomId, String status) {
        try{
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoomEntity.setStatus(ClassRoomStatus.valueOf(status));
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1ada");
            notificationEntity.setTitle("Classroom "+classRoomEntity.getName()+" has been " + status);
            notificationEntity.setMessage("Your Class:  " + classRoomEntity.getName() + " has been " + status);
            notificationEntity.setAuthorId(classRoomEntity.getId());
            notificationEntity.setTargetUrl(classRoomEntity.getId());
            notificationEntity.setPriority(NotificationPriority.NORMAL);
            List<String> ids= new ArrayList<>();
            List<StudentEnrollmentsEntity> studentEnrollmentsEntities = studentEnrollmentsRepository.findByClassroomId(classroomId);
            for (StudentEnrollmentsEntity studentEnrollmentsEntity : studentEnrollmentsEntities){
                StudentEntity studentEntity = studentRepository.findById(studentEnrollmentsEntity.getStudentId()).get();

                ids.add(studentEntity.getUserId());
            }
            TeacherEntity teacherEntity = teacherRepository.findById(classRoomEntity.getTeacherId()).get();
            ids.add(teacherEntity.getUserId());
            notificationService.createNotification(notificationEntity,ids);

            classRoomRepository.save(classRoomEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomRankinResponse getClassRoomRanking(String classroomId, int page, int size, Integer rating) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewEntity> reviewPage;

            if (rating != null) {
                reviewPage = reviewRepository.findByClassroomIdAndRating(classroomId, rating, pageable);
            } else {
                reviewPage = reviewRepository.findByClassroomId(classroomId, pageable);
            }

            List<GetClassRoomRankinResponse.Review> reviews = reviewPage.getContent().stream()
                    .map(this::mapReviewEntityToResponse)
                    .collect(Collectors.toList());

            long totalReviews = reviewPage.getTotalElements();
            List<ReviewEntity> allReviews = reviewRepository.findByClassroomId(classroomId);
            double averageRating = allReviews.stream()
                    .mapToDouble(ReviewEntity::getRating)
                    .average()
                    .orElse(0.0);
            GetClassRoomRankinResponse response = new GetClassRoomRankinResponse();
            response.setTotalElement(totalReviews);
            response.setTotalPage(reviewPage.getTotalPages());
            response.setAverageRating(averageRating);
            response.setTotalReview((int) totalReviews);
            response.setData(reviews);

            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void sendApprovalClassroomRequest(ApprovalClassroomRequestEntity body) {
        try {
            ClassRoomEntity classRoom = classRoomRepository.findById(body.getClassroomId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            TeacherEntity teacher = teacherRepository.findByUserId(body.getTeacherId());

            if (!classRoom.getTeacherId().equals(teacher.getId())) {
                throw new IllegalArgumentException("TeacherId is not authorized to approve classroom request");
            }

            ApprovalClassroomRequestEntity  approvalClassroomRequestEntity = approvalClassroomRepository.findByClassroomIdAndTeacherId(body.getClassroomId(),body.getTeacherId());
            if (approvalClassroomRequestEntity == null) {
                approvalClassroomRequestEntity = new ApprovalClassroomRequestEntity();
            }
            approvalClassroomRequestEntity.setClassroomId(body.getClassroomId());
            approvalClassroomRequestEntity.setTeacherId(body.getTeacherId());
            approvalClassroomRequestEntity.setStatus(body.getStatus());
            approvalClassroomRequestEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            approvalClassroomRequestEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            approvalClassroomRepository.save(approvalClassroomRequestEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private GetClassRoomRankinResponse.Review mapReviewEntityToResponse(ReviewEntity reviewEntity) {
        GetClassRoomRankinResponse.Review review = new GetClassRoomRankinResponse.Review();
        review.setId(reviewEntity.getId());
        review.setTitle(reviewEntity.getTitle());
        review.setComment(reviewEntity.getContent());
        review.setRating(reviewEntity.getRating());
        review.setAuthorId(reviewEntity.getUserId());
        review.setClassroomId(reviewEntity.getClassroomId());
        review.setCreatedAt(reviewEntity.getCreatedAt());
        review.setUpdatedAt(reviewEntity.getUpdatedAt());
        // Assuming you have a method to fetch user details
        UserEntity user = userRepository.findById(reviewEntity.getUserId()).orElse(null);
        if (user != null) {
            review.setAuthorName(user.getFullname());
            review.setAuthorAvatar(user.getAvatar());
        }
        return review;
    }
}
