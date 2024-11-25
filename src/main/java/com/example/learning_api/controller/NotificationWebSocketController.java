package com.example.learning_api.controller;

import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final INotificationService notificationService;

    @MessageMapping("/notifications.markAsSeen")
    public void markNotificationAsSeen(@Payload String notificationId,
                                       SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getUser().getName();
        notificationService.markNotificationAsSeen(userId, notificationId);

        // Send confirmation back to user
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications.seen",
                notificationId
        );
    }
}