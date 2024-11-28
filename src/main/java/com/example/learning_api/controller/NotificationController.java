package com.example.learning_api.controller;

import com.example.learning_api.dto.request.notification.SendNotificationRequest;
import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final INotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // API gửi thông báo
    @PostMapping("/send")
    public ResponseEntity<ResponseAPI<NotificationEntity>> sendNotification(
            @RequestBody SendNotificationRequest request) {
        try {
            NotificationEntity notification = notificationService.createNotification(
                    NotificationEntity.builder()
                            .title(request.getTitle())
                            .message(request.getMessage())
                            .authorId(request.getAuthorId())
                            .targetUrl(request.getTargetUrl())
                            .priority(request.getPriority())
                            .notificationSettingId(request.getNotificationSettingId())
                            .build(),
                    request.getReceiverIds()
            );

            // Gửi realtime
            request.getReceiverIds().forEach(userId -> {
                messagingTemplate.convertAndSendToUser(
                        userId,
                        "/topic/notifications",
                        notification
                );
            });

            return ResponseEntity.ok(
                    ResponseAPI.<NotificationEntity>builder()
                            .data(notification)
                            .message("Notification sent successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<NotificationEntity>builder()
                            .message("Failed to send notification")
                            .build()
            );
        }
    }

    // Lấy danh sách thông báo của người dùng
    @GetMapping("/user")
    public ResponseEntity<ResponseAPI<List<NotificationEntity>>> getUserNotifications(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<NotificationEntity> notifications = notificationService.getUserNotifications(userId, page, size);
            return ResponseEntity.ok(
                    ResponseAPI.<List<NotificationEntity>>builder()
                            .data(notifications)
                            .message("User notifications retrieved")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving user notifications", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<List<NotificationEntity>>builder()
                            .message("Failed to retrieve notifications")
                            .build()
            );
        }
    }

    // Đánh dấu thông báo đã đọc
    @PostMapping("/mark-seen")
    public ResponseEntity<ResponseAPI<Void>> markNotificationAsSeen(
            @RequestParam String userId,
            @RequestParam String notificationId) {
        try {
            notificationService.markNotificationAsSeen(userId, notificationId);
            return ResponseEntity.ok(
                    ResponseAPI.<Void>builder()
                            .message("Notification marked as seen")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error marking notification as seen", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<Void>builder()
                            .message("Failed to mark notification as seen")
                            .build()
            );
        }
    }
}
