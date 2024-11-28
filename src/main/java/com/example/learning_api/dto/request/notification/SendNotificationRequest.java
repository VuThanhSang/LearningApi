package com.example.learning_api.dto.request.notification;


import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

import java.util.List;

@Data
public class SendNotificationRequest {
    private String title;
    private String message;
    private String authorId;
    private RoleEnum authorRole;
    private NotificationFormType type;
    private NotificationPriority priority;
    private String notificationSettingId;
    private List<String> receiverIds;
    private String targetUrl;
    private String expiresAt;
}