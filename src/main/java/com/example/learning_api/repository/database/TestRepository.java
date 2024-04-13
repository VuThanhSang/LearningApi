package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<TestEntity, String> {
}
