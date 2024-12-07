package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.BlockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlockRepository extends MongoRepository<BlockEntity, String> {
    List<BlockEntity> findByDocumentId(String documentId);
}
