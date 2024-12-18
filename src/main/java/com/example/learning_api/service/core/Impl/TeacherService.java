package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.deadline.GetUpcomingDeadlineResponse;
import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.cart.GetPaymentForTeacher;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeacherPopularResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.dto.response.teacher.TeacherDashboardResponse;
import com.example.learning_api.dto.response.test.GetTestInProgress;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.TeacherStatus;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService implements ITeacherService {
    private final ModelMapperService modelMapperService;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final SubjectSpecializationRepository subjectSpecializationRepository;
    private final MajorsRepository majorsRepository;
    private final DeadlineRepository deadlineRepository;
    private final TestRepository testRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final ClassRoomRepository classroomRepository;
    private final ReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;
    private final StudentRepository studentRepository;
    private final ClassRoomRepository classRoomRepository;
    @Override
    public CreateTeacherResponse createTeacher(CreateTeacherRequest body) {
        try{
            UserEntity userEntity = userRepository.findById(body.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userEntity==null){
                throw new IllegalArgumentException("UserId is not found");
            }
            CreateTeacherResponse resData = new CreateTeacherResponse();
            TeacherEntity teacherEntity = modelMapperService.mapClass(body, TeacherEntity.class);
            teacherEntity.setUser(userEntity);
            teacherRepository.save(teacherEntity);
            resData.setUser(userRepository.findById(body.getUserId()).get());
            resData.setUserId(body.getUserId());
            resData.setBio(body.getBio());
            resData.setQualifications(body.getQualifications());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateTeacher(UpdateTeacherRequest body) {
        try{
            TeacherEntity teacherEntity = teacherRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            if (body.getBio()!=null){
                teacherEntity.setBio(body.getBio());
            }
            if (body.getQualifications()!=null){
                teacherEntity.setQualifications(body.getQualifications());
            }
            if (body.getPhone()!=null){
                teacherEntity.setPhone(body.getPhone());
            }
            if (body.getAddress()!=null){
                teacherEntity.setAddress(body.getAddress());
            }
            if (body.getGender()!=null){
                teacherEntity.setGender(body.getGender());
            }
            if (body.getDateOfBirth()!=null){
                teacherEntity.setDateOfBirth(body.getDateOfBirth());
            }
            if (body.getExperience()!=null){
                teacherEntity.setExperience(body.getExperience());
            }
            if (body.getStatus()!=null){
                teacherEntity.setStatus(TeacherStatus.valueOf(body.getStatus()));
            }
            teacherRepository.save(teacherEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteTeacher(String id) {
        try{
            teacherRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetTeachersResponse getTeachers(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<TeacherEntity> teacherEntities = teacherRepository.findByNameContaining(search, pageAble);
            GetTeachersResponse resData = new GetTeachersResponse();
            List<GetTeachersResponse.TeacherResponse> teacherResponses = new ArrayList<>();
            for (TeacherEntity teacherEntity : teacherEntities){
                GetTeachersResponse.TeacherResponse teacherResponse = modelMapperService.mapClass(teacherEntity, GetTeachersResponse.TeacherResponse.class);
                teacherResponses.add(teacherResponse);
            }
            resData.setTeachers(teacherResponses);
            resData.setTotalElements(teacherEntities.getTotalElements());
            resData.setTotalPage(teacherEntities.getTotalPages());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void addSubjectSpecialization(String teacherId, String majorId) {
        try{
            TeacherEntity teacherEntity = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            if (teacherEntity==null){
                throw new IllegalArgumentException("Teacher not found");
            }
            if (majorId==null){
                throw new IllegalArgumentException("MajorId is required");
            }
            if (majorsRepository.findById(majorId)==null){
                throw new IllegalArgumentException("MajorId is not found");
            }
            SubjectSpecializationEntity subjectSpecializationEntity = new SubjectSpecializationEntity();
            subjectSpecializationEntity.setTeacherId(teacherId);
            subjectSpecializationEntity.setMajorsId(majorId);
            subjectSpecializationRepository.save(subjectSpecializationEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public TeacherEntity getTeacherByUserId(String teacherId) {
        try{
            return teacherRepository.findByUserId(teacherId);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public TeacherDashboardResponse getTeacherDashboard(String teacherId) {
        try {
            TeacherDashboardResponse resData = new TeacherDashboardResponse();
            List<ClassRoomEntity> classRoomEntities = classroomRepository.findByTeacherId(teacherId);
            resData.setTotalClasses(classRoomEntities.size());
            List<String> classroomIds = new ArrayList<>();
            for (ClassRoomEntity classRoomEntity : classRoomEntities) {
                classroomIds.add(classRoomEntity.getId());
            }
            List<StudentEnrollmentsEntity> studentEnrollmentsEntities = studentEnrollmentsRepository.findByClassroomIdIn(classroomIds);
            resData.setTotalStudents(studentEnrollmentsEntities.size());
            List<TransactionEntity> transactionEntities = transactionRepository.findByClassroomIdIn(classroomIds);
            int totalRevenue = 0;
            for (TransactionEntity transactionEntity : transactionEntities) {
                totalRevenue += transactionEntity.getAmount();
            }
            resData.setTotalRevenue(totalRevenue);
            List<ReviewEntity> reviewEntities = reviewRepository.findByClassroomIdIn(classroomIds);
            int totalRating = 0;
            for (ReviewEntity reviewEntity : reviewEntities) {
                totalRating += reviewEntity.getRating();
            }
            resData.setAverageRating((double) totalRating / reviewEntities.size());
            List<TeacherDashboardResponse.RecentSales> recentSales = new ArrayList<>();
            List<TeacherDashboardResponse.Review> reviews = new ArrayList<>();
            for (TransactionEntity transactionEntity : transactionEntities) {
                TeacherDashboardResponse.RecentSales recentSale = new TeacherDashboardResponse.RecentSales();
                UserEntity userEntity = userRepository.findById(transactionEntity.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                ClassRoomEntity classRoomEntity = classroomRepository.findById(transactionEntity.getClassroomId())
                        .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));
                recentSale.setStudentName(userEntity.getFullname());
                recentSale.setStudentAvatar(userEntity.getAvatar());
                recentSale.setClassName(classRoomEntity.getName());
                recentSale.setClassroomId(transactionEntity.getClassroomId());
                recentSale.setPrice(Math.toIntExact(transactionEntity.getAmount()));
                recentSales.add(recentSale);
            }
            for (ReviewEntity reviewEntity : reviewEntities) {
                UserEntity userEntity = userRepository.findById(reviewEntity.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                ClassRoomEntity classRoomEntity = classroomRepository.findById(reviewEntity.getClassroomId())
                        .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));
                TeacherDashboardResponse.Review review = new TeacherDashboardResponse.Review();
                review.setStudentName(userEntity.getFullname());
                review.setStudentAvatar(userEntity.getAvatar());
                review.setClassName(classRoomEntity.getName());
                review.setClassroomId(reviewEntity.getClassroomId());
                review.setRating(reviewEntity.getRating());
                review.setComment(reviewEntity.getContent());
                reviews.add(review);
            }
            resData.setRecentSales(recentSales.size() > 5 ? recentSales.subList(0, 5) : recentSales);
            resData.setReviews(reviews.size() > 5 ? reviews.subList(0, 5) : reviews);
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetPaymentForTeacher getPaymentForTeacher(String teacherId, int page, int size, String sort, String order, String status, String search, String searchBy, String createdAtRange) {
        try {
            // Validate sorting order
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders; Must be 'asc' or 'desc'");
            }

            // Configure pageable
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(upperOrder), sort));

            // Determine time boundaries for `createdAt`
            Long startTimestamp = null;
            Long endTimestamp = System.currentTimeMillis();

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
            } else {
                startTimestamp = LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }

            String startTimestampStr = String.valueOf(startTimestamp);
            String endTimestampStr = String.valueOf(endTimestamp);

            // Fetch classrooms for the teacher
            List<ClassRoomEntity> classRoomEntities = classroomRepository.findByTeacherId(teacherId);
            List<String> classroomIds = classRoomEntities.stream().map(ClassRoomEntity::getId).toList();

            // Handle searchBy logic (filter by user or classroom name)
            List<String> filteredClassroomIds = new ArrayList<>();
            List<String> userIds = new ArrayList<>();
            if (!search.isEmpty()) {
                if ("user".equalsIgnoreCase(searchBy)) {
                    List<UserEntity> users = userRepository.findIdsByFullnameRegex(search);
                    userIds = users.stream().map(UserEntity::getId).toList();
                } else if ("class".equalsIgnoreCase(searchBy)) {
                    List<ClassRoomEntity> filteredClassrooms = classRoomRepository.findIdsByNameRegex(search);
                    filteredClassroomIds = filteredClassrooms.stream().map(ClassRoomEntity::getId).toList();
                    classroomIds = filteredClassroomIds;
                } else {
                    throw new IllegalArgumentException("Invalid value '" + searchBy + "' for searchBy; Must be 'user' or 'class'");
                }
            }

            // Fetch transactions based on filters
            Page<TransactionEntity> transactionEntities;
            if (status.isEmpty()) {
                if (!userIds.isEmpty()) {
                    transactionEntities = transactionRepository.findByUserIdInAndClassroomIdInAndCreatedAtBetween(userIds, classroomIds, startTimestampStr, endTimestampStr, pageable);
                } else if (!filteredClassroomIds.isEmpty()) {
                    transactionEntities = transactionRepository.findByClassroomIdInAndCreatedAtBetween(filteredClassroomIds, startTimestampStr, endTimestampStr, pageable);
                } else {
                    transactionEntities = transactionRepository.findByClassroomIdInAndCreatedAtBetween(classroomIds, startTimestampStr, endTimestampStr, pageable);
                }
            } else {
                if (!userIds.isEmpty()) {
                    transactionEntities = transactionRepository.findByStatusAndUserIdInAndClassroomIdInAndCreatedAtBetween(status, userIds, classroomIds, startTimestampStr, endTimestampStr, pageable);
                } else if (!filteredClassroomIds.isEmpty()) {
                    transactionEntities = transactionRepository.findByStatusAndClassroomIdInAndCreatedAtBetween(status, filteredClassroomIds, startTimestampStr, endTimestampStr, pageable);
                } else {
                    transactionEntities = transactionRepository.findByStatusAndClassroomIdInAndCreatedAtBetween(status, classroomIds, startTimestampStr, endTimestampStr, pageable);
                }
            }

            // Prepare response data
            GetPaymentForTeacher resData = new GetPaymentForTeacher();
            List<GetPaymentForTeacher.Transaction> transactions = new ArrayList<>();

            // Map transaction data
            for (TransactionEntity transactionEntity : transactionEntities) {
                GetPaymentForTeacher.Transaction transaction = modelMapperService.mapClass(transactionEntity, GetPaymentForTeacher.Transaction.class);

                // Fetch user details
                UserEntity userEntity = userRepository.findById(transactionEntity.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId());
                if (studentEntity != null) {
                    studentEntity.setUser(null);
                    userEntity.setStudent(studentEntity);
                }
                transaction.setUser(userEntity);

                // Fetch classroom details
                ClassRoomEntity classRoomEntity = classroomRepository.findById(transactionEntity.getClassroomId()).orElse(null);

                transaction.setClassroom(classRoomEntity);
                transaction.setTransactionId(transactionEntity.getId());
                transactions.add(transaction);
            }

            // Set response data
            resData.setTransactions(transactions);
            resData.setTotalClassroom((long) classRoomEntities.size());
            resData.setTotalElement(transactionEntities.getTotalElements());
            resData.setTotalPage(transactionEntities.getTotalPages());
            resData.setTotalPrice(transactionEntities.stream().mapToLong(TransactionEntity::getAmount).sum());

            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public List<GetTeacherPopularResponse> getTeacherPopular(int page, int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            List<GetTeacherPopularResponse> resData = new ArrayList<>();
            List<TeacherEntity> teacherEntities = teacherRepository.findAll(pageable).getContent();
            for (TeacherEntity teacherEntity : teacherEntities){
                UserEntity userEntity = userRepository.findById(teacherEntity.getUserId()).orElse(null);
                GetTeacherPopularResponse teacherPopularResponse = new GetTeacherPopularResponse();
                teacherPopularResponse.setId(teacherEntity.getId());
                teacherPopularResponse.setFullname(userEntity.getFullname());
                teacherPopularResponse.setAvatar(userEntity.getAvatar());
                teacherPopularResponse.setExperience(teacherEntity.getExperience());
                List<ClassRoomEntity> classRoomEntities = classroomRepository.findByTeacherId(teacherEntity.getId());
                List<String> classroomIds = new ArrayList<>();
                for (ClassRoomEntity classRoomEntity : classRoomEntities){
                    classroomIds.add(classRoomEntity.getId());
                }
                List<StudentEnrollmentsEntity> studentEnrollmentsEntities = studentEnrollmentsRepository.findByClassroomIdIn(classroomIds);
                teacherPopularResponse.setNumberStudent((long) studentEnrollmentsEntities.size());
                resData.add(teacherPopularResponse);

            }
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
