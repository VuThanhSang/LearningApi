package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestFeedbackAnswerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TestFeedbackAnswerRepository extends MongoRepository<TestFeedbackAnswerEntity, String> {
    List<TestFeedbackAnswerEntity> findByTestFeedbackId(String testFeedbackId);
}
