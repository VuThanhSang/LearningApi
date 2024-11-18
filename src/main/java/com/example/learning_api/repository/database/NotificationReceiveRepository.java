package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationReceiveEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationReceiveRepository extends MongoRepository<NotificationReceiveEntity, String> {
}
