package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.notification.SendNotificationRequest;
import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.entity.sql.database.NotificationReceiveEntity;
import com.example.learning_api.entity.sql.database.NotificationSettingsEntity;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface INotificationService {

    // 1. Tạo notification mới
    @Transactional
    NotificationEntity createNotification(NotificationEntity notification, List<String> receiverIds);

    // 2. Filter receivers dựa trên preferences của họ
    List<String> filterReceiversByPreferences(List<String> receiverIds, String notificationSettingId);

    // 3. Tạo notification receives với delivery method phù hợp
    List<NotificationReceiveEntity> createNotificationReceives(
            List<String> receiverIds,
            NotificationEntity notification,
            NotificationSettingsEntity settings);

    // 4. Xác định delivery method cho user
    String determineDeliveryMethod(String userId, NotificationSettingsEntity settings);

    // 5. Lấy thống kê notification theo settings
    Map<String, Object> getNotificationStats(String notificationSettingId);

    // 2. Đánh dấu notification đã đọc
    @Transactional
    void markNotificationAsSeen(String userId, String notificationId);

    // 3. Lấy danh sách notification của user
    List<NotificationEntity> getUserNotifications(String userId, int page, int size);

    // 4. Cập nhật user notification settings
    @Transactional
    void updateUserNotificationSettings(String userId, String notificationTypeId, boolean enabled, String deliveryMethod);

    // 5. Xóa notification (soft delete)
    @Transactional
    void deleteNotification(String notificationId);

    // 6. Kiểm tra và cleanup expired notifications
    @Transactional
    void cleanupExpiredNotifications();
}