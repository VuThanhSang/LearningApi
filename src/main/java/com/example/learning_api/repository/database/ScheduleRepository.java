package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ScheduleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<ScheduleEntity, String> {
}
