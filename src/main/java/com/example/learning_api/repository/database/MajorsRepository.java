package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MajorsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MajorsRepository extends MongoRepository<MajorsEntity, String> {
}
