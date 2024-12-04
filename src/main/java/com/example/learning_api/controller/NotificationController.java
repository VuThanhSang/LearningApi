package com.example.learning_api.controller;

import com.example.learning_api.dto.request.notification.SendNotificationRequest;
import com.example.learning_api.dto.request.notification.UpdateUserNotificationSettingRequest;
import com.example.learning_api.dto.response.notification.NotificationResponse;
import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.entity.sql.database.NotificationSettingsEntity;
import com.example.learning_api.entity.sql.database.UserNotificationSettingsEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
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
    private final JwtService jwtService;

    @PostMapping("/send")
    public ResponseEntity<ResponseAPI<NotificationResponse>> sendNotification(
            @RequestBody SendNotificationRequest request) {
        try {
            NotificationResponse notification = notificationService.createNotification(
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



            return ResponseEntity.ok(
                    ResponseAPI.<NotificationResponse>builder()
                            .data(notification)
                            .message("Notification sent successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<NotificationResponse>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    // Lấy danh sách thông báo của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseAPI<List<NotificationEntity>>> getUserNotifications(
            @PathVariable String userId,
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

    @PostMapping("/update-user-settings")
    public ResponseEntity<ResponseAPI<Void>> updateUserNotificationSettings(
            @RequestBody UpdateUserNotificationSettingRequest request,

            @RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.substring(7);
            String currentUserId = jwtService.extractUserId(token);
            request.setUserId(currentUserId);
            notificationService.updateUserNotificationSettings(request);
            return ResponseEntity.ok(
                    ResponseAPI.<Void>builder()
                            .message("User notification settings updated")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error updating user notification settings", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<Void>builder()
                            .message("Failed to update user notification settings")
                            .build()
            );
        }
    }
    @GetMapping("/user-settings")
    public ResponseEntity<ResponseAPI<List<UserNotificationSettingsEntity>>> getUserNotificationSettings(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "search", required = false, defaultValue = "") String search) {
        try {
            String token = authorizationHeader.substring(7);
            String currentUserId = jwtService.extractUserId(token);
            List<UserNotificationSettingsEntity> settings = notificationService.getUserNotificationSettings(currentUserId, search);
            return ResponseEntity.ok(
                    ResponseAPI.<List<UserNotificationSettingsEntity>>builder()
                            .data(settings)
                            .message("User notification settings retrieved")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving user notification settings", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<List<UserNotificationSettingsEntity>>builder()
                            .message("Failed to retrieve user notification settings")
                            .build()
            );
        }
    }

    @GetMapping("/search-settings")
    public ResponseEntity<ResponseAPI<List<NotificationSettingsEntity>>> searchNotificationSettings(
            @RequestParam(name = "keyword", required = false, defaultValue = "")  String keyword) {
        try {
            List<NotificationSettingsEntity> settings = notificationService.searchNotificationSettings(keyword);
            return ResponseEntity.ok(
                    ResponseAPI.<List<NotificationSettingsEntity>>builder()
                            .data(settings)
                            .message("Notification settings retrieved")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error searching notification settings", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<List<NotificationSettingsEntity>>builder()
                            .message("Failed to search notification settings")
                            .build()
            );
        }
    }


}
