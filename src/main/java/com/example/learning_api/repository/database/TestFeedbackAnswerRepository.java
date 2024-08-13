package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestFeedbackAnswerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestFeedbackAnswerRepository extends MongoRepository<TestFeedbackAnswerEntity, String> {
}
