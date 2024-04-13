package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SubstanceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubstanceRepository extends MongoRepository<SubstanceEntity, String> {
}
