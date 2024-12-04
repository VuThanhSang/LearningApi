package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class MarkNotificationSeenDto {
    private String notificationId;
    private String userId;
}
