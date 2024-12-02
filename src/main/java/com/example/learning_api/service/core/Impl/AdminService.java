package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.common.TotalTestOfDayDto;
import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;
import com.example.learning_api.dto.response.admin.GetUsersResponse;
import com.example.learning_api.dto.response.classroom.TotalClassroomOfDayDto;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.UserStatus;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public GetAdminDashboardResponse getAdminDashboard() {
        try {

            String startOfWeek = "2024-06-01"; // replace with your startOfWeek
            String endOfWeek = "2024-06-08"; // replace with your endOfWeek

            // Get the tests in the week
            List<TotalTestOfDayDto> testsInWeek = testRepository.findTestsInWeek(startOfWeek, endOfWeek);

            // Create a default list with 7 days and count = 0
            List<TotalTestOfDayDto> defaultDaysTest = Arrays.asList(
                    new TotalTestOfDayDto("Sunday", 0),
                    new TotalTestOfDayDto("Monday", 0),
                    new TotalTestOfDayDto("Tuesday", 0),
                    new TotalTestOfDayDto("Wednesday", 0),
                    new TotalTestOfDayDto("Thursday", 0),
                    new TotalTestOfDayDto("Friday", 0),
                    new TotalTestOfDayDto("Saturday", 0)
            );

            // Array of week days

            // Merge the results from the query into the default list
            defaultDaysTest.forEach(day -> {
                testsInWeek.stream()
                        .filter(result -> result.get_id().equals(day.get_id()))
                        .findFirst()
                        .ifPresent(result -> day.setCount(result.getCount()));
            });

            List<TotalClassroomOfDayDto> totalClassroom = classRoomRepository.countSessionsByDayOfWeek();

            // Tạo danh sách mặc định với 7 ngày và count = 0
            List<TotalClassroomOfDayDto> defaultDaysClassroom = Arrays.asList(
                    new TotalClassroomOfDayDto("Sunday", 0),
                    new TotalClassroomOfDayDto("Monday", 0),
                    new TotalClassroomOfDayDto("Tuesday", 0),
                    new TotalClassroomOfDayDto("Wednesday", 0),
                    new TotalClassroomOfDayDto("Thursday", 0),
                    new TotalClassroomOfDayDto("Friday", 0),
                    new TotalClassroomOfDayDto("Saturday", 0)
            );

            // Gộp kết quả từ truy vấn vào danh sách mặc định
            defaultDaysClassroom.forEach(day -> {
                totalClassroom.stream()
                        .filter(result -> result.get_id().equals(day.get_id()))
                        .findFirst()
                        .ifPresent(result -> day.setCount(result.getCount()));
            });
            int totalCourse = classRoomRepository.findAll().size();
            int totalStudent = studentRepository.findAll().size();
            int totalTeacher = teacherRepository.findAll().size();

            GetAdminDashboardResponse resData = new GetAdminDashboardResponse();
            GetAdminDashboardResponse.ClassroomAndTest classroomAndTest = new GetAdminDashboardResponse.ClassroomAndTest();
            classroomAndTest.setTotalClassroom(defaultDaysClassroom);
            classroomAndTest.setTotalTest(defaultDaysTest);

            resData.setScheduleAndTest(classroomAndTest);
            resData.setTotalCourse(totalCourse);
            resData.setTotalStudent(totalStudent);
            resData.setTotalTeacher(totalTeacher);
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetUsersResponse getTeachers(String search, int page, int size, String sort, String order, String status) {
        try {
            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
            Page<TeacherEntity> teacherEntities;
            if (status.isEmpty()) {
                teacherEntities = teacherRepository.findByNameContaining(search, pageable);
            } else {
                teacherEntities = teacherRepository.findByNameContainingAndStatus(search, status, pageable);
            }
            GetUsersResponse resData = new GetUsersResponse();
            List<UserEntity> userEntities = new ArrayList<>();
            for (TeacherEntity teacherEntity : teacherEntities) {
                UserEntity userEntity = teacherEntity.getUser();
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
    public GetUsersResponse getStudents(String search, int page, int size, String sort, String order, String status) {
        try {
            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
                throw new IllegalArgumentException("Invalid value '" + order + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
            Page<StudentEntity> studentEntities;
            if (status.isEmpty()) {
                studentEntities = studentRepository.findByNameContaining(search, pageable);
            } else {
                studentEntities = studentRepository.findByNameContainingAndStatus(search, status, pageable);
            }
            GetUsersResponse resData = new GetUsersResponse();
            List<UserEntity> userEntities = new ArrayList<>();
            for (StudentEntity studentEntity : studentEntities) {
                UserEntity userEntity = studentEntity.getUser();
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

}
