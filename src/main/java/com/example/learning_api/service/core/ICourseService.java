package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.course.CreateCourseRequest;
import com.example.learning_api.dto.request.course.DeleteCourseRequest;
import com.example.learning_api.dto.request.course.UpdateCourseRequest;
import com.example.learning_api.dto.response.course.CreateCourseResponse;
import com.example.learning_api.dto.response.course.GetCoursesResponse;

public interface ICourseService {
    CreateCourseResponse createCourse(CreateCourseRequest body);
    void updateCourse(UpdateCourseRequest body);
    void deleteCourse(DeleteCourseRequest courseId);
    GetCoursesResponse getCourses(int page, int size, String search);
}
