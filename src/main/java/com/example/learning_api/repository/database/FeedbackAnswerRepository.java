package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FeedbackAnswerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackAnswerRepository extends MongoRepository<FeedbackAnswerEntity, String> {
    List<FeedbackAnswerEntity> findByFeedbackId(String testFeedbackId);
}
