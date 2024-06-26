package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MeetingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeetingRepository extends MongoRepository<MeetingEntity, String> {
}
