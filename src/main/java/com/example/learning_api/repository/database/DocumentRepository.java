package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
}
