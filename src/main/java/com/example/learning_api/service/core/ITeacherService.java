package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.deadline.GetUpcomingDeadlineResponse;
import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.cart.GetPaymentForTeacher;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeacherPopularResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.dto.response.teacher.TeacherDashboardResponse;
import com.example.learning_api.dto.response.test.GetTestInProgress;
import com.example.learning_api.entity.sql.database.TeacherEntity;

import java.util.List;

public interface ITeacherService {
    CreateTeacherResponse createTeacher(CreateTeacherRequest body);
    void updateTeacher( UpdateTeacherRequest body);
    void deleteTeacher(String id);
    GetTeachersResponse getTeachers(int page, int size, String search);
    void addSubjectSpecialization(String teacherId, String majorId);
    TeacherEntity getTeacherByUserId(String teacherId);
    TeacherDashboardResponse getTeacherDashboard(String teacherId);
    GetPaymentForTeacher getPaymentForTeacher(String teacherId, int page, int size, String sort, String order, String status, String search, String searchBy, String createdAtRange);
    List<GetTeacherPopularResponse> getTeacherPopular(int page, int size);
}
