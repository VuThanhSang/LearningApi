package com.example.learning_api.service.core;

public interface IStudentEnrollmentsService {
    void enrollStudent(String studentId, String courseId);
    void dropStudent(String studentId, String courseId);
    void completeStudent(String studentId, String courseId);
    void updateStudentGrade(String studentId, String courseId, int grade);

}
