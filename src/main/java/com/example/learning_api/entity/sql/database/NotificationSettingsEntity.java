package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsEntity {
   @Id
   private String id;

   @Indexed(unique = true)
   private String notificationType;

   private String description;

   @Builder.Default
   private Boolean enabledByDefault = true;

   @Builder.Default
   private Boolean isRequired = false;

   private String category;

   // Thêm các cài đặt specific cho LMS
   private Integer reminderDays;  // Số ngày trước để nhắc nhở
   private List<String> allowedRoles;  // Các role được phép nhận notification này
   private List<String> deliveryMethods;  // Các phương thức gửi được hỗ trợ

   // Cài đặt về tần suất
   private Integer maxFrequencyPerDay;  // Số lần tối đa/ngày
   private Integer minIntervalMinutes;  // Khoảng cách tối thiểu giữa 2 notification

   @Builder.Default
   private String createdAt = String.valueOf(System.currentTimeMillis());
   private String updatedAt;
   @Builder.Default
   private Boolean isDeleted = false;
}
