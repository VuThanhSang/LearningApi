package com.example.learning_api.controller;

import com.example.learning_api.dto.common.MarkNotificationSeenDto;
import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final INotificationService notificationService;
    @MessageMapping("/notifications.markAsSeen")
    public void markNotificationAsSeen(@Payload MarkNotificationSeenDto markNotificationSeenDto) {
        try {
            // Truy xuất notificationId và userId từ DTO
            String notificationId = markNotificationSeenDto.getNotificationId();
            String userId = markNotificationSeenDto.getUserId();

            // Gọi service xử lý logic
            notificationService.markNotificationAsSeen(userId, notificationId);

            // Gửi phản hồi lại cho client
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/notifications.seen",
                    notificationId
            );
        } catch (Exception e) {
            logger.error("Error processing markNotificationAsSeen: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}