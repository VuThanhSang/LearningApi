package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.UserNotificationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserNotificationSettingsRepository extends MongoRepository<UserNotificationSettingsEntity, String> {
    Optional<UserNotificationSettingsEntity> findByUserIdAndNotificationSettingId(String userId, String notificationSettingId);

}
