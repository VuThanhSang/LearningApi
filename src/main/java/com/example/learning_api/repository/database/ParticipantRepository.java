package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {
}
