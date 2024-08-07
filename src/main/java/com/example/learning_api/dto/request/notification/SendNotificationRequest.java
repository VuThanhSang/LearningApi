package com.example.learning_api.dto.request.notification;


import lombok.Data;

@Data
public class SendNotificationRequest {
    private String senderId;
    private String senderRole;
    private String title;
    private String message;
    private String formId;
    private String formType;
    private String type;

}
