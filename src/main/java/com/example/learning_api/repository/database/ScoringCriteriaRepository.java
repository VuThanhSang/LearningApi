package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ScoringCriteriaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ScoringCriteriaRepository extends MongoRepository<ScoringCriteriaEntity, String>{
    int countByDeadlineId(String deadlineId);
    List<ScoringCriteriaEntity> findByDeadlineId(String deadlineId);
}
