package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TestResultRepository extends MongoRepository<TestResultEntity, String> {
    @Query(value = "{studentId: ?0, testId: ?1, state: 'FINISHED'}")
    List<TestResultEntity> findByStudentIdAndTestId(String studentId, String testId);

    void deleteByStudentIdAndTestId(String studentId, String testId);
    void deleteByTestId(String testId);
    @Query(value = "{studentId: ?0, testId: ?1}", count = true)
    int countByStudentIdAndTestId(String studentId, String testId);
    TestResultEntity findFirstByStudentIdAndTestIdAndStateOrderByAttendedAtDesc(String studentId, String testId, String state);
}