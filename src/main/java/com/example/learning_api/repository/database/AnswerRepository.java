package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.AnswerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswerRepository extends MongoRepository<AnswerEntity, String>{

}
