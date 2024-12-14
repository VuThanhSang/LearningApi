package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
    List<NotificationEntity> findByNotificationSettingId(String notificationSettingId);

    List<NotificationEntity> findByExpiresAtLessThanAndIsDeletedFalse(String expiresAt);

    Long countByAuthorIdAndCreatedAtAfter(String authorId, String createdAt);

    List<NotificationEntity> findTopByAuthorIdOrderByCreatedAtDesc(String authorId);

    List<NotificationEntity> findByAuthorId(String authorId);
}
