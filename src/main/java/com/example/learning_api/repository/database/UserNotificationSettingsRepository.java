package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.UserNotificationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserNotificationSettingsRepository extends MongoRepository<UserNotificationSettingsEntity, String> {
    Optional<UserNotificationSettingsEntity> findByUserIdAndNotificationSettingId(String userId, String notificationSettingId);
    List<UserNotificationSettingsEntity> findByUserId(String userId);
    @Query("{'userId': ?0, 'notificationSettingName': {$regex: ?1, $options: 'i'}}")
    List<UserNotificationSettingsEntity> findByUserIdAndNotificationNameRegex(String userId, String keyword);
}
