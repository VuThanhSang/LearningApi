package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestResultRepository extends MongoRepository<TestResultEntity, String> {

    TestResultEntity findByStudentIdAndTestId(String studentId, String testId);

    void deleteByStudentIdAndTestId(String studentId, String testId);
}
