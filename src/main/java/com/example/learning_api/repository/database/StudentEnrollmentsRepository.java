package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentEnrollmentsRepository extends MongoRepository<StudentEnrollmentsEntity, String>{
    void deleteByStudentIdAndCourseId(String studentId, String courseId);
    @Query("{'studentId': ?0, 'courseId': ?1}")
    StudentEnrollmentsEntity findByStudentIdAndCourseId(String studentId, String courseId);
}
