package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.student_enrollments.EnrollStudentRequest;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.enums.StudentEnrollmentStatus;
import com.example.learning_api.repository.database.*;
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
    private final ClassRoomRepository classroomRepository;
    @Override
    public void enrollStudent(EnrollStudentRequest body) {
        try {
            if (body.getStudentId() == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
             if (studentRepository.findById(body.getStudentId()).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            ClassRoomEntity classRoomEntity = classroomRepository.findById(body.getClassroomId()).orElse(null);
            if (classRoomEntity == null) {
                throw new IllegalArgumentException("ClassroomId is not found");
            }
            if (classRoomEntity.getEnrollmentCapacity() <= classRoomEntity.getCurrentEnrollment()) {
                throw new IllegalArgumentException("Classroom is full");
            }

            StudentEnrollmentsEntity studentEnrollmentsEntity = new StudentEnrollmentsEntity();
            studentEnrollmentsEntity.setStudentId(body.getStudentId());
            studentEnrollmentsEntity.setClassroomId(body.getClassroomId());
            studentEnrollmentsEntity.setGrade("0");
            studentEnrollmentsEntity.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
            classRoomEntity.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment() + 1);
            classroomRepository.save(classRoomEntity);
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
//            studentEnrollmentsRepository.deleteByStudentIdAndCourseId(studentId, courseId);
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
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
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
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
