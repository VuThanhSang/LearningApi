package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String>{
}
