package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class NotificationEntity {
    @Id
    private String id;

    @Indexed
    private String receiverId;

    private String title;
    private String message;

    @Indexed
    private String formId;

    private NotificationFormType formType;
    private String type;

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private boolean isDeleted = false;

    @Indexed
    private String senderId;

    private RoleEnum senderRole;
    private RoleEnum receiverRole;

    private String createdAt;

    private String updatedAt;
}