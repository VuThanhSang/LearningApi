package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.entity.sql.database.NotificationReceiveEntity;
import com.example.learning_api.entity.sql.database.NotificationSettingsEntity;
import com.example.learning_api.entity.sql.database.UserNotificationSettingsEntity;
import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.core.INotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationReceiveRepository notificationReceiveRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;

    // 1. Tạo notification mới
    @Transactional
    @Override
    public NotificationEntity createNotification(NotificationEntity notification, List<String> receiverIds) {
        // Validate notification settings
        NotificationSettingsEntity settings = notificationSettingsRepository
                .findById(notification.getNotificationSettingId())
                .orElseThrow(() -> new RuntimeException("Notification settings not found"));

        // Validate receivers against allowed roles (if specified)
        if (settings.getAllowedRoles() != null && !settings.getAllowedRoles().isEmpty()) {
            // Implement role validation logic here
            validateReceiverRoles(receiverIds, settings.getAllowedRoles());
        }

        // Kiểm tra frequency limit
        validateNotificationFrequency(notification.getAuthorId(), settings);

        // Filter receivers based on their notification preferences
        List<String> filteredReceivers = filterReceiversByPreferences(receiverIds, notification.getNotificationSettingId());

        // Lưu notification
        notification.setTotalReceivers(filteredReceivers.size());
        notification.setType(NotificationFormType.valueOf(settings.getNotificationType()));
        NotificationEntity savedNotification = notificationRepository.save(notification);

        // Tạo notification receive cho từng user với delivery method từ preferences
        List<NotificationReceiveEntity> receives = createNotificationReceives(filteredReceivers, savedNotification, settings);
        notificationReceiveRepository.saveAll(receives);

        return savedNotification;
    }
    // 2. Filter receivers dựa trên preferences của họ
    @Override
    public List<String> filterReceiversByPreferences(List<String> receiverIds, String notificationSettingId) {
        return receiverIds.stream()
                .filter(userId -> {
                    UserNotificationSettingsEntity userSettings = userNotificationSettingsRepository
                            .findByUserIdAndNotificationSettingId(userId, notificationSettingId)
                            .orElse(null);

                    // Nếu user chưa có settings, dùng default từ notification settings
                    if (userSettings == null) {
                        NotificationSettingsEntity settings = notificationSettingsRepository
                                .findById(notificationSettingId)
                                .orElseThrow(() -> new RuntimeException("Notification settings not found"));
                        return settings.getEnabledByDefault();
                    }

                    return userSettings.getEnabled();
                })
                .collect(Collectors.toList());
    }

    // 3. Tạo notification receives với delivery method phù hợp
    @Override
    public List<NotificationReceiveEntity> createNotificationReceives(
            List<String> receiverIds,
            NotificationEntity notification,
            NotificationSettingsEntity settings) {
        return receiverIds.stream()
                .map(userId -> {
                    String deliveryMethod = determineDeliveryMethod(userId, settings);
                    return NotificationReceiveEntity.builder()
                            .notificationId(notification.getId())
                            .userId(userId)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 4. Xác định delivery method cho user
    @Override
    public String determineDeliveryMethod(String userId, NotificationSettingsEntity settings) {
        return userNotificationSettingsRepository
                .findByUserIdAndNotificationSettingId(userId, settings.getId())
                .map(UserNotificationSettingsEntity::getDeliveryMethod)
                .orElse(settings.getDeliveryMethods().get(0)); // Use first available method as default
    }

    // 5. Lấy thống kê notification theo settings
    @Override
    public Map<String, Object> getNotificationStats(String notificationSettingId) {
        List<NotificationEntity> notifications = notificationRepository
                .findByNotificationSettingId(notificationSettingId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSent", notifications.size());
        stats.put("totalSeen", notifications.stream().mapToInt(NotificationEntity::getSeenCount).sum());
        stats.put("totalReceivers", notifications.stream().mapToInt(NotificationEntity::getTotalReceivers).sum());

        // Thêm các thống kê khác nếu cần
        return stats;
    }
    // 2. Đánh dấu notification đã đọc
    @Transactional
    @Override
    public void markNotificationAsSeen(String userId, String notificationId) {
        NotificationReceiveEntity receive = notificationReceiveRepository
                .findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new RuntimeException("Notification receive not found"));

        if (!receive.getSeen()) {
            receive.setSeen(true);
            receive.setSeenAt(String.valueOf(System.currentTimeMillis()));
            notificationReceiveRepository.save(receive);

            // Update seen count trong notification
            NotificationEntity notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.setSeenCount(notification.getSeenCount() + 1);
            notificationRepository.save(notification);
        }
    }

    // 3. Lấy danh sách notification của user
    @Override
    public List<NotificationEntity> getUserNotifications(String userId, int page, int size) {
        List<NotificationReceiveEntity> receives = notificationReceiveRepository
                .findByUserIdOrderByReceivedAtDesc(userId);

        return receives.stream()
                .map(receive -> notificationRepository.findById(receive.getNotificationId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(notification -> !notification.getIsDeleted())
                .collect(Collectors.toList());
    }

    // 4. Cập nhật user notification settings
    @Transactional
    @Override
    public void updateUserNotificationSettings(String userId, String notificationTypeId, boolean enabled, String deliveryMethod) {
        UserNotificationSettingsEntity settings = userNotificationSettingsRepository
                .findByUserIdAndNotificationSettingId(userId, notificationTypeId)
                .orElse(UserNotificationSettingsEntity.builder()
                        .userId(userId)
                        .notificationSettingId(notificationTypeId)
                        .build());

        settings.setEnabled(enabled);
        settings.setDeliveryMethod(deliveryMethod);
        settings.setUpdatedAt(String.valueOf(System.currentTimeMillis()));

        userNotificationSettingsRepository.save(settings);
    }

    // 5. Xóa notification (soft delete)
    @Transactional
    @Override
    public void deleteNotification(String notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsDeleted(true);
        notification.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        notificationRepository.save(notification);
    }

    // 6. Kiểm tra và cleanup expired notifications
    @Transactional
    @Override
    public void cleanupExpiredNotifications() {
        List<NotificationEntity> expiredNotifications = notificationRepository
                .findByExpiresAtLessThanAndIsDeletedFalse(String.valueOf(System.currentTimeMillis()));

        expiredNotifications.forEach(notification -> {
            notification.setIsDeleted(true);
            notification.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        });

        notificationRepository.saveAll(expiredNotifications);
    }

    // Helper method để validate frequency
    private void validateNotificationFrequency(String authorId, NotificationSettingsEntity settings) {
        if (settings.getMaxFrequencyPerDay() != null) {
            long dailyCount = notificationRepository.countByAuthorIdAndCreatedAtAfter(
                    authorId,
                    String.valueOf(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            );

            if (dailyCount >= settings.getMaxFrequencyPerDay()) {
                throw new RuntimeException("Daily notification limit exceeded");
            }
        }

        if (settings.getMinIntervalMinutes() != null) {
            long lastNotificationTime = notificationRepository
                    .findTopByAuthorIdOrderByCreatedAtDesc(authorId)
                    .stream()
                    .map(notification -> Long.parseLong(notification.getCreatedAt()))
                    .findFirst()
                    .orElse(0L);

            long minInterval = settings.getMinIntervalMinutes() * 60 * 1000;
            if (System.currentTimeMillis() - lastNotificationTime < minInterval) {
                throw new RuntimeException("Minimum interval between notifications not met");
            }
        }
    }
    private void validateReceiverRoles(List<String> receiverIds, List<String> allowedRoles) {
        // Implement validation logic here
        // Ví dụ: check role của mỗi receiver có trong allowedRoles không
    }


}