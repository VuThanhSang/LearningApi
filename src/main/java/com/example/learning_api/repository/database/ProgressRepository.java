package com.example.learning_api.repository.database;


import com.example.learning_api.entity.sql.database.ProgressEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends MongoRepository<ProgressEntity, String> {
    // Lấy tiến trình của học viên theo classroom và section
    ProgressEntity findByStudentIdAndClassroomIdAndSectionId(String studentId, String classroomId, String sectionId);

    // Lấy tiến trình của học viên theo classroom và bài học
    Optional<ProgressEntity> findByStudentIdAndClassroomIdAndLessonId(String studentId, String classroomId, String lessonId);

    boolean existsByStudentIdAndClassroomIdAndLessonIdAndCompleted(String studentId, String classroomId, String lessonId, Boolean completed);

    // Kiểm tra section đã hoàn thành hay chưa
    boolean existsByStudentIdAndClassroomIdAndSectionIdAndCompleted(String studentId, String classroomId, String sectionId, Boolean completed);
    List<ProgressEntity> findByClassroomIdAndLessonIdInAndCompletedAndStudentId(String classroomId, List<String> lessonId, Boolean completed, String studentId);
}