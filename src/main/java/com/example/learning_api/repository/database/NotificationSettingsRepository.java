package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationSettingsRepository extends MongoRepository<NotificationSettingsEntity, String>{
}
