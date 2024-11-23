package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.student_enrollments.EnrollStudentRequest;
import com.example.learning_api.dto.response.classroom.GetStudentInClassResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.dto.response.student.StudentsResponse;

public interface IStudentEnrollmentsService {
    void enrollStudent(EnrollStudentRequest body);
    void dropStudent(String studentId, String courseId);
    void completeStudent(String studentId, String courseId);
    void updateStudentGrade(String studentId, String courseId, int grade);
    GetStudentInClassResponse getStudentInClass(String classroomId,Integer page, Integer limit, String search, String sort, String order);
    StudentsResponse getStudents(Integer page, Integer limit, String search, String sort, String order, String classroomId);
}
