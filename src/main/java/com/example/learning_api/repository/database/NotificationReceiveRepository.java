package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationReceiveEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationReceiveRepository extends MongoRepository<NotificationReceiveEntity, String> {
    Optional<NotificationReceiveEntity> findByUserIdAndNotificationId(String userId, String NotificationId);
    List<NotificationReceiveEntity> findByUserIdOrderByReceivedAtDesc(String userId);
}
