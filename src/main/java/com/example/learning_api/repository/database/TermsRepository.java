package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TermsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TermsRepository extends MongoRepository<TermsEntity, String> {
}
