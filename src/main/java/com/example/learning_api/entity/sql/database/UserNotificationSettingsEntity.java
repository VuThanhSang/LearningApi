package com.example.learning_api.entity.sql.database;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "user_notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationSettingsEntity {
   @Id
   private String id;

   private String userId;

   private String notificationSettingId;

   @Builder.Default
   private Boolean enabled = true;

   private String deliveryMethod;  // EMAIL, PUSH, IN_APP, SMS

   @Builder.Default
   private String createdAt = String.valueOf(System.currentTimeMillis());

   private String updatedAt;

   @Builder.Default
   private Boolean isDeleted = false;
}
