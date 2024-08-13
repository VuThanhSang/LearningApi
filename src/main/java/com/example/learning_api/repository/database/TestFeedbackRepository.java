package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestFeedbackEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestFeedbackRepository extends MongoRepository<TestFeedbackEntity, String> {
}
