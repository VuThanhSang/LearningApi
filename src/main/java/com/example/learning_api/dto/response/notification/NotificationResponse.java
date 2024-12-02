package com.example.learning_api.dto.response.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String authorId;
    private String authorRole;
    private String notificationSettingId;
    private String status;
    private String priority;
    private String expiresAt;
    private String type;
    private String targetUrl;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
    private Integer totalReceivers;
    private List<String> receiversId;
}
