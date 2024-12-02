package com.example.learning_api.dto.request.notification;

import lombok.Data;

@Data
public class UpdateUserNotificationSettingRequest {
    private String userId;
    private String notificationSettingId;
    private Boolean enabled;
    private String deliveryMethod;

}
