package com.example.learning_api.dto.common;
import com.example.learning_api.enums.NotificationFormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
    private String id;
    private String title;
    private String message;
    private String authorId;
    private NotificationFormType type;
    private String targetUrl;
    private String[] receiverIds;
}