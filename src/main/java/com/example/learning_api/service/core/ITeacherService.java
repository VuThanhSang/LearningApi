package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;

public interface ITeacherService {
    CreateTeacherResponse createTeacher(CreateTeacherRequest body);
    void updateTeacher( UpdateTeacherRequest body);
    void deleteTeacher(String id);
    GetTeachersResponse getTeachers(int page, int size, String search);
}
