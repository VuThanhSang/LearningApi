package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.BlockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<BlockEntity, String> {
}
