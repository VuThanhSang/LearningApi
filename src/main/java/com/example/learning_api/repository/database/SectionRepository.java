package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SectionRepository extends MongoRepository<SectionEntity, String> {
}
