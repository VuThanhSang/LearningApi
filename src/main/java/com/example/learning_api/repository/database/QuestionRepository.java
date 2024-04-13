package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.QuestionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<QuestionEntity, String>{
}
