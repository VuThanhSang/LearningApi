package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NotificationSettingsRepository extends MongoRepository<NotificationSettingsEntity, String>{
    @Query("{'notificationType': {$regex: ?0, $options: 'i'}}")
    List<NotificationSettingsEntity> findByNameContainingIgnoreCase (String keyword);
}
