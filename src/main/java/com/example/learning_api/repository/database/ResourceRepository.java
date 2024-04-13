package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ResourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceRepository extends MongoRepository<ResourceEntity, String> {
}
