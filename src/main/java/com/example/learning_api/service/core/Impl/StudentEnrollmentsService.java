package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.enums.StudentEnrollmentStatus;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.core.IStudentEnrollmentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentEnrollmentsService implements IStudentEnrollmentsService {
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    @Override
    public void enrollStudent(String studentId, String courseId) {
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
             if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = new StudentEnrollmentsEntity();
            studentEnrollmentsEntity.setStudentId(studentId);
            studentEnrollmentsEntity.setCourseId(courseId);
            studentEnrollmentsEntity.setGrade("0");
            studentEnrollmentsEntity.setEnrolledAt(new Date());
            studentEnrollmentsEntity.setCreatedAt(new Date());
            studentEnrollmentsEntity.setUpdatedAt(new Date());
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);

        } catch (Exception e) {

            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void dropStudent(String studentId, String courseId) {
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            studentEnrollmentsRepository.deleteByStudentIdAndCourseId(studentId, courseId);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void completeStudent(String studentId, String courseId) {
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndCourseId(studentId, courseId);
            studentEnrollmentsEntity.setStatus(StudentEnrollmentStatus.COMPLETED);
            studentEnrollmentsEntity.setUpdatedAt(new Date());
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }



    @Override
    public void updateStudentGrade(String studentId, String courseId, int grade) {
        // TODO Auto-generated method stub
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndCourseId(studentId, courseId);
            studentEnrollmentsEntity.setGrade(String.valueOf(grade));
            studentEnrollmentsEntity.setUpdatedAt(new Date());
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
