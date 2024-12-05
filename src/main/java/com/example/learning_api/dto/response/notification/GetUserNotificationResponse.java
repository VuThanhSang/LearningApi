package com.example.learning_api.dto.response.notification;

import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.enums.NotificationStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
@Data
public class GetUserNotificationResponse {
    private String id;

    private String title;

    private String message;

    private String authorId;

    private RoleEnum authorRole;
    private String notificationSettingId;

    private NotificationStatus status = NotificationStatus.PENDING;
    private NotificationPriority priority = NotificationPriority.NORMAL;
    private String expiresAt; // Thời gian hết hạn

    private NotificationFormType type;

    private String targetUrl; // URL liên kết khi click vào notification

    private String createdAt = String.valueOf(System.currentTimeMillis());

    private String updatedAt;
    private Boolean seen = false;
}
