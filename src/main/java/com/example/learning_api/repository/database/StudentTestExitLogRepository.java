package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.StudentTestExitLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StudentTestExitLogRepository extends MongoRepository<StudentTestExitLogEntity, String> {
    List<StudentTestExitLogEntity> findByStudentIdAndTestResultId(String studentId, String testResultId);
}
