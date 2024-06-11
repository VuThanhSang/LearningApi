package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.student_enrollments.EnrollStudentRequest;

public interface IStudentEnrollmentsService {
    void enrollStudent(EnrollStudentRequest body);
    void dropStudent(String studentId, String courseId);
    void completeStudent(String studentId, String courseId);
    void updateStudentGrade(String studentId, String courseId, int grade);

}
