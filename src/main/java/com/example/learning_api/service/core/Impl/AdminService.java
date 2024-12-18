package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.common.StudentSubmissionCountDto;
import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.*;
import com.example.learning_api.dto.response.cart.GetPaymentForTeacher;
import com.example.learning_api.dto.response.classroom.GetApprovalClassroomResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.*;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.core.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {

    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final TestRepository testRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final FileRepository fileRepository;
    private final CloudinaryService cloudinaryService;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final ForumRepository forumRepository;
    private final NotificationRepository notificationRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final ApprovalClassroomRepository approvalClassroomRequestRepository;
    @Override
    public void changeRole(ChangeRoleRequest body) {
        try {
            userRepository.findById(body.getUserId()).ifPresent(userEntity -> {
                userEntity.setRole(RoleEnum.valueOf(body.getRole()));
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void deleteAccount(String userId) {
        try {
            userRepository.findById(userId).ifPresent(userEntity -> {
                userEntity.setStatus(UserStatus.BLOCKED);
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void blockAccount(String userId) {
        try {
            userRepository.findById(userId).ifPresent(userEntity -> {
                userEntity.setStatus(UserStatus.BLOCKED);
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void removeFile(String fileId) {
        try {
            fileRepository.findById(fileId).ifPresent(fileEntity -> {
                try {
                    cloudinaryService.deleteImage(fileEntity.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fileRepository.delete(fileEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void updateStatus(String userId, String status) {
        try {
            userRepository.findById(userId).ifPresent(userEntity -> {
                userEntity.setStatus(UserStatus.valueOf(status));
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void updateForumStatus(String forumId, String status) {
        try {
            forumRepository.findById(forumId).ifPresent(forumEntity -> {
                forumEntity.setStatus(ForumStatus.valueOf(status));
                forumRepository.save(forumEntity);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetAdminDashboardResponse getAdminDashboard() {
        try{
            int totalTeacher = (int) userRepository.countByRole(RoleEnum.TEACHER.name());
            int totalStudent = (int) userRepository.countByRole(RoleEnum.USER.name());
            int totalClassroom = (int) classRoomRepository.count();
            List<StudentSubmissionCountDto> studentSubmissionCountDto = studentEnrollmentsRepository.getMonthlyEnrollmentStats();
            List<StudentSubmissionCountDto> classroomPerformance = studentEnrollmentsRepository.getTopEnrolledClassrooms();
            List<GetAdminDashboardResponse.EnrollmentTrend> enrollmentTrends = new ArrayList<>();
            List<GetAdminDashboardResponse.ClassroomPerformance> classroomPerformances = new ArrayList<>();
            for (StudentSubmissionCountDto studentSubmissionCount : studentSubmissionCountDto) {
                GetAdminDashboardResponse.EnrollmentTrend enrollmentTrend = new GetAdminDashboardResponse.EnrollmentTrend();
                enrollmentTrend.setDate(studentSubmissionCount.get_id());
                enrollmentTrend.setTotal(studentSubmissionCount.getEnrollmentCount());
                enrollmentTrends.add(enrollmentTrend);
            }
            for (StudentSubmissionCountDto studentSubmissionCount : classroomPerformance) {
                GetAdminDashboardResponse.ClassroomPerformance classroomPerformance1 = new GetAdminDashboardResponse.ClassroomPerformance();
                classroomPerformance1.setId(studentSubmissionCount.get_id());
                classroomPerformance1.setTotalStudent(studentSubmissionCount.getEnrollmentCount());
                ClassRoomEntity classRoomEntity = classRoomRepository.findById(studentSubmissionCount.get_id()).orElse(null);
                if (classRoomEntity != null) {
                    classroomPerformance1.setName(classRoomEntity.getName());
                }
                classroomPerformances.add(classroomPerformance1);
            }
            GetAdminDashboardResponse resData = new GetAdminDashboardResponse();
            GetAdminDashboardResponse.UserEngagement userEngagement = new GetAdminDashboardResponse.UserEngagement();
            userEngagement.setTotalActiveUser((int) userRepository.countByStatus(UserStatus.ACTIVE.name()));
            userEngagement.setTotalBlockUser((int) userRepository.countByStatus(UserStatus.BLOCKED.name()));
            userEngagement.setTotalInactiveUser((int) userRepository.countByStatus(UserStatus.INACTIVE.name()));
            resData.setTotalTeacher(totalTeacher);
            resData.setTotalStudent(totalStudent);
            resData.setTotalClassroom(totalClassroom);
            resData.setEnrollmentTrend(enrollmentTrends);
            resData.setClassroomPerfomance(classroomPerformances);
            resData.setTotalEnrollmentInMonth(studentSubmissionCountDto.get(studentSubmissionCountDto.size()-1).getEnrollmentCount());
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<NotificationEntity> notificationEntities = notificationRepository.findAll();
            List<GetRecentActivity> recentActivities = new ArrayList<>();
            int size  = Math.min(notificationEntities.size(), 5);
            for (int i = 0; i < size; i++) {
                GetRecentActivity getRecentActivity = new GetRecentActivity();
                getRecentActivity.setTitle(notificationEntities.get(i).getTitle());
                getRecentActivity.setTime(notificationEntities.get(i).getCreatedAt());
                recentActivities.add(getRecentActivity);
            }
            resData.setRecentActivity(recentActivities);
            resData.setUserEngagement(userEngagement);
            return resData;

        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }
    @Override
    public GetUsersResponse getTeachers(String search, int page, int size,  String sort,String order,String status) {
        try {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(upperOrder), sort));
            Page<TeacherEntity> teacherEntities;
            if (status.isEmpty()) {
                teacherEntities = teacherRepository.findByNameContaining(search, pageable);
            } else {
                teacherEntities = teacherRepository.findByNameContainingAndStatus(search, status, pageable);
            }
            GetUsersResponse resData = new GetUsersResponse();
            List<UserEntity> userEntities = new ArrayList<>();
            for (TeacherEntity teacherEntity : teacherEntities) {
                UserEntity userEntity = userRepository.findById(teacherEntity.getUserId()).orElse(null);
                teacherEntity.setUser(null);
                userEntity.setTeacher(teacherEntity);
                userEntities.add(userEntity);
            }
            resData.setData(userEntities);
            resData.setTotalElements(teacherEntities.getTotalElements());
            resData.setTotalPage(teacherEntities.getTotalPages());
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetUsersResponse getStudents(String search, int page, int size,  String sort,String order, String status) {
        try {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(upperOrder), sort));
            Page<StudentEntity> studentEntities;
            if (status.isEmpty()) {
                studentEntities = studentRepository.findByNameContaining(search, pageable);
            } else {
                studentEntities = studentRepository.findByNameContainingAndStatus(search, status, pageable);
            }
            GetUsersResponse resData = new GetUsersResponse();
            List<UserEntity> userEntities = new ArrayList<>();
            for (StudentEntity studentEntity : studentEntities) {
                UserEntity userEntity = userRepository.findById(studentEntity.getUserId()).orElse(null);
                studentEntity.setUser(null);
                userEntity.setStudent(studentEntity);
                userEntities.add(userEntity);
            }
            resData.setData(userEntities);
            resData.setTotalElements(studentEntities.getTotalElements());
            resData.setTotalPage(studentEntities.getTotalPages());
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomsAdminResponse getClassRooms(String search, int page, int size, String sort, String order, String status) {
        try {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(upperOrder), sort));
            Page<ClassRoomEntity> classRoomEntities;
            if (status.isEmpty()) {
                classRoomEntities = classRoomRepository.findByNameContainingForAdmin(search, pageable);
            } else {
                classRoomEntities = classRoomRepository.findByNameContainingAndStatusForAdmin(search, status, pageable);
            }
            for (ClassRoomEntity classRoomEntity : classRoomEntities) {
                classRoomEntity.setCurrentEnrollment(studentEnrollmentsRepository.countByClassroomId(classRoomEntity.getId()));
            }
            GetClassRoomsAdminResponse resData = new GetClassRoomsAdminResponse();
            resData.setData(classRoomEntities.getContent());
            resData.setTotalElements(classRoomEntities.getTotalElements());
            resData.setTotalPage(classRoomEntities.getTotalPages());
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetUserDetailResponse getUserDetail(String userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            Pageable pageAble = PageRequest.of(0, 99);
            List<ClassRoomEntity> classRoomEntities = new ArrayList<>();
            if (userEntity.getRole().equals(RoleEnum.TEACHER)) {
                TeacherEntity teacherEntity = teacherRepository.findByUserId(userId);
                if (teacherEntity == null) {
                    throw new IllegalArgumentException("Teacher not found");
                }
                classRoomEntities = classRoomRepository.findByTeacherIdAndNameContaining(teacherEntity.getId(), "", pageAble).getContent();
                teacherEntity.setUser(null);
                userEntity.setTeacher(teacherEntity);

            } else if (userEntity.getRole().equals(RoleEnum.USER)) {
                StudentEntity studentEntity = studentRepository.findByUserId(userId);
                if (studentEntity == null) {
                    throw new IllegalArgumentException("Student not found");
                }
                List<String> classRoomIds = studentEnrollmentsRepository.findByStudentId(studentEntity.getId()).stream()
                        .map(StudentEnrollmentsEntity::getClassroomId)
                        .collect(Collectors.toList());
                classRoomEntities= classRoomRepository.findByIdInAndNameContaining(classRoomIds, "", pageAble).getContent();
                studentEntity.setUser(null);
                userEntity.setStudent(studentEntity);

            }
            GetUserDetailResponse resData = new GetUserDetailResponse();
            resData.setUser(userEntity);
            resData.setClassRooms(classRoomEntities);

            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createCategory(CategoryEntity category) {
        try {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setName(category.getName());
            categoryEntity.setDescription(category.getDescription());
            categoryEntity.setTotalClassRoom(0);
            categoryEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            categoryEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            categoryRepository.save(categoryEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateCategory(String id, CategoryEntity name) {
        try {
            CategoryEntity categoryEntity = categoryRepository.findById(id).orElse(null);
            if (categoryEntity == null) {
                throw new IllegalArgumentException("Category not found");
            }
            if (name.getName() != null)
                categoryEntity.setName(name.getName());
            if (name.getDescription() != null)
                categoryEntity.setDescription(name.getDescription());

            categoryEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            categoryRepository.save(categoryEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }


    @Override
    public void deleteCategory(String id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<CategoryEntity> getCategories(String name) {
        try {
            return categoryRepository.findAllSortByTotalClassRoomDescAndNameContaining(name);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetPaymentForTeacher getPaymentForAdmin(int page, int size, String sort, String order, String status, String search, String searchBy, String createdAtRange) {
        try {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders; Has to be either 'desc' or 'asc' (case insensitive)");
            }

            // Determine time boundaries for `createdAt` as timestamps in milliseconds
            Long startTimestamp = null;
            Long endTimestamp = System.currentTimeMillis(); // Current time in milliseconds

            if ("TODAY".equalsIgnoreCase(createdAtRange)) {
                startTimestamp = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else if ("WEEK".equalsIgnoreCase(createdAtRange)) {
                startTimestamp = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else if ("MONTH".equalsIgnoreCase(createdAtRange)) {
                startTimestamp = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else if ("YEAR".equalsIgnoreCase(createdAtRange)) {
                startTimestamp = LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }else{
                startTimestamp = LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            String startTimestampStr = String.valueOf(startTimestamp);
            String endTimestampStr = String.valueOf(endTimestamp);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(upperOrder), sort));
            List<String> userIds = new ArrayList<>();
            List<String> classroomIds = new ArrayList<>();

            // Handle search by user or classroom
            if (!search.isEmpty()) {
                if ("user".equalsIgnoreCase(searchBy)) {
                    List<UserEntity> users = userRepository.findIdsByFullnameRegex(search);
                    userIds = users.stream().map(UserEntity::getId).collect(Collectors.toList());
                } else if ("class".equalsIgnoreCase(searchBy)) {
                    List<ClassRoomEntity> classrooms = classRoomRepository.findIdsByNameRegex(search);
                    classroomIds = classrooms.stream().map(ClassRoomEntity::getId).collect(Collectors.toList());
                } else {
                    throw new IllegalArgumentException("Invalid value '" + searchBy + "' for searchBy; Must be 'user' or 'class'");
                }
            }

            // Fetch transactions based on filters
            Page<TransactionEntity> transactionEntities;
            if (status.isEmpty()) {
                transactionEntities = fetchByFiltersWithoutStatus(pageable, userIds, classroomIds, startTimestampStr, endTimestampStr);
            } else {
                transactionEntities = fetchByFiltersWithStatus(pageable, status, userIds, classroomIds, startTimestampStr, endTimestampStr);
            }

            // Process transactions into the DTO
            return processTransactions(transactionEntities);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetApprovalClassroomResponse getApprovalClassrooms(int page, int size, String search, String sort, String order, String status) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
            List<ClassRoomEntity> classrooms = classRoomRepository.findByNameContaining(search);
            List<String> classroomIds = classrooms.stream().map(ClassRoomEntity::getId).collect(Collectors.toList());

            Page<ApprovalClassroomRequestEntity> approvalRequests;
            if (status.isEmpty()) {
                approvalRequests = approvalClassroomRequestRepository.findByClassroomIds(classroomIds, pageable);
            } else {
                approvalRequests = approvalClassroomRequestRepository.findByClassroomIdsAndStatus(classroomIds, status, pageable);
            }

            GetApprovalClassroomResponse response = new GetApprovalClassroomResponse();
            response.setTotalPage(approvalRequests.getTotalPages());
            response.setTotalElement((int) approvalRequests.getTotalElements());
            response.setData(approvalRequests.getContent().stream().map(request -> {
                GetApprovalClassroomResponse.ApprovalRequest approvalRequest = new GetApprovalClassroomResponse.ApprovalRequest();
                approvalRequest.setId(request.getId());
                approvalRequest.setClassroomId(request.getClassroomId());
                approvalRequest.setTeacherId(request.getTeacherId());
                UserEntity teacher = userRepository.findById(request.getTeacherId()).orElse(null);
                approvalRequest.setTeacher(teacher);
                approvalRequest.setStatus(request.getStatus().name());
                approvalRequest.setCreatedAt(request.getCreatedAt());
                approvalRequest.setUpdatedAt(request.getUpdatedAt());

                ClassRoomEntity classroom = classRoomRepository.findById(request.getClassroomId()).orElse(null);
                approvalRequest.setClassroom(classroom);

                return approvalRequest;
            }).collect(Collectors.toList()));

            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void approveClassroom(String id, String status) {
        try {
            ApprovalClassroomRequestEntity approvalRequest = approvalClassroomRequestRepository.findById(id).orElse(null);
            if (approvalRequest == null) {
                throw new IllegalArgumentException("Approval request not found");
            }

            approvalRequest.setStatus(ApprovalClassStatus.valueOf(status));
//            NotificationEntity notificationEntity = new NotificationEntity();
//            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1ada");
//            notificationEntity.setTitle("Classroom "+classRoomEntity.getName()+" has been " + status);
//            notificationEntity.setMessage("Your Class:  " + classRoomEntity.getName() + " has been " + status);
//            notificationEntity.setAuthorId(classRoomEntity.getId());
//            notificationEntity.setTargetUrl(classRoomEntity.getId());
//            notificationEntity.setPriority(NotificationPriority.NORMAL);
            List<String> ids= new ArrayList<>();
            if (status.equals(ApprovalClassStatus.APPROVED.name())) {
                ClassRoomEntity classroom = classRoomRepository.findById(approvalRequest.getClassroomId()).orElse(null);
                if (classroom != null) {
                    classroom.setStatus(ClassRoomStatus.COMPLETED);
                    classRoomRepository.save(classroom);
                }
            }
            approvalClassroomRequestRepository.save(approvalRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private GetPaymentForTeacher processTransactions(Page<TransactionEntity> transactionEntities) {
        GetPaymentForTeacher resData = new GetPaymentForTeacher();
        List<GetPaymentForTeacher.Transaction> transactions = new ArrayList<>();

        for (TransactionEntity transactionEntity : transactionEntities) {
            GetPaymentForTeacher.Transaction transaction = new GetPaymentForTeacher.Transaction();

            // Map basic fields
            transaction.setTransactionId(transactionEntity.getId());
            transaction.setAmount(transactionEntity.getAmount());
            transaction.setTransactionRef(transactionEntity.getTransactionRef());
            transaction.setStatus(transactionEntity.getStatus());
            transaction.setPaymentMethod(transactionEntity.getPaymentMethod());
            transaction.setCreatedAt(transactionEntity.getCreatedAt());
            transaction.setUpdatedAt(transactionEntity.getUpdatedAt());

            // Fetch and map user information
            UserEntity userEntity = userRepository.findById(transactionEntity.getUserId()).orElse(null);
            if (userEntity != null) {
                StudentEntity studentEntity = studentRepository.findByUserId(transactionEntity.getUserId());
                if (studentEntity != null) {
                    studentEntity.setUser(null); // Avoid circular reference
                }
                userEntity.setStudent(studentEntity);
                transaction.setUser(userEntity);
            }

            // Fetch and map classroom information
            ClassRoomEntity classroomEntity = classRoomRepository.findById(transactionEntity.getClassroomId()).orElse(null);
            transaction.setClassroom(classroomEntity);

            transactions.add(transaction);
        }

        // Set response metadata
        resData.setTransactions(transactions);
        resData.setTotalClassroom((long) classRoomRepository.count());
        resData.setTotalElement(transactionEntities.getTotalElements());
        resData.setTotalPage(transactionEntities.getTotalPages());
        resData.setTotalPrice(transactionEntities.stream().mapToLong(TransactionEntity::getAmount).sum());

        return resData;
    }
    private Page<TransactionEntity> fetchByFiltersWithoutStatus(Pageable pageable, List<String> userIds, List<String> classroomIds, String startTimestamp, String endTimestamp) {
        if (!userIds.isEmpty()) {
            return transactionRepository.findByUserIdInAndCreatedAtBetween(userIds, startTimestamp, endTimestamp, pageable);
        } else if (!classroomIds.isEmpty()) {
            return transactionRepository.findByClassroomIdInAndCreatedAtBetween(classroomIds, startTimestamp, endTimestamp, pageable);
        } else {
            return transactionRepository.findAllByCreatedAtBetween(startTimestamp, endTimestamp, pageable);
        }
    }

    private Page<TransactionEntity> fetchByFiltersWithStatus(Pageable pageable, String status, List<String> userIds, List<String> classroomIds, String startTimestamp, String endTimestamp) {
        if (!userIds.isEmpty()) {
            return transactionRepository.findByStatusAndUserIdInAndCreatedAtBetween(status, userIds, startTimestamp, endTimestamp, pageable);
        } else if (!classroomIds.isEmpty()) {
            return transactionRepository.findByStatusAndClassroomIdInAndCreatedAtBetween(status, classroomIds, startTimestamp, endTimestamp, pageable);
        } else {
            return transactionRepository.findByStatusAndCreatedAtBetween(status, startTimestamp, endTimestamp, pageable);
        }
    }

}
