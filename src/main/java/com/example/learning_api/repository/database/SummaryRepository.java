package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SummaryEntity;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SummaryRepository extends MongoRepository<SummaryEntity, String> {
    SummaryEntity findByStudentIdAndTermIdAndCourseId(String studentId, String termId, String courseId);
    List<SummaryEntity> findByStudentIdAndTermId(String studentId, String termId);
    List<SummaryEntity> findByStudentId(String studentId);
}
