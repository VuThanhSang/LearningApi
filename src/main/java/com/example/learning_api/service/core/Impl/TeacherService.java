package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.deadline.GetUpcomingDeadlineResponse;
import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
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
import org.springframework.stereotype.Service;

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


}
