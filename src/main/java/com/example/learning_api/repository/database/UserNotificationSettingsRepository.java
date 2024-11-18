package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.UserNotificationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserNotificationSettingsRepository extends MongoRepository<UserNotificationSettingsEntity, String> {
}
