package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FAQEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FAQRepository extends MongoRepository<FAQEntity, String>{
}
