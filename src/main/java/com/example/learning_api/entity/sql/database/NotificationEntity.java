package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.enums.NotificationStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    @Id
    private String id;

    @Indexed
    private String title;

    private String message;

    @Indexed
    private String authorId;

    private RoleEnum authorRole;

    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.NORMAL;
    private String expiresAt;    // Thời gian hết hạn

    private NotificationFormType type;

    private String targetUrl;  // URL liên kết khi click vào notification

    @Builder.Default
    private String createdAt = String.valueOf(System.currentTimeMillis());

    private String updatedAt;

    @Builder.Default
    private Boolean isDeleted = false;

    // Thêm các trường thống kê
    @Builder.Default
    private Integer seenCount = 0;

    @Builder.Default
    private Integer totalReceivers = 0;
}
