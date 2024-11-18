package com.example.learning_api.entity.sql.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "notification_receives")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReceiveEntity {
    @Id
    private String id;

    private String notificationId;

    private String userId;

    @Builder.Default
    private Boolean seen = false;

    @Builder.Default
    private String receivedAt = String.valueOf(System.currentTimeMillis());

    private String seenAt;

    @Builder.Default
    private Boolean isDeleted = false;

    // Thêm trường action để theo dõi hành động của user
    private String actionTaken;  // CLICKED, DISMISSED, etc.

    private String actionTakenAt;
}