package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.notification.UpdateUserNotificationSettingRequest;
import com.example.learning_api.dto.response.notification.NotificationResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.NotificationFormType;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.core.INotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final NotificationReceiveRepository notificationReceiveRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    // 1. Tạo notification mới
    @Transactional
    @Override
    @Async
    public NotificationResponse createNotification(NotificationEntity notification, List<String> receiverIds) {
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
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(savedNotification.getId())
                .title(savedNotification.getTitle())
                .message(savedNotification.getMessage())
                .authorId(savedNotification.getAuthorId())
                .notificationSettingId(savedNotification.getNotificationSettingId())
                .status(savedNotification.getStatus().name())
                .priority(savedNotification.getPriority().name())
                .expiresAt(savedNotification.getExpiresAt())
                .type(savedNotification.getType().name())
                .targetUrl(savedNotification.getTargetUrl())
                .createdAt(savedNotification.getCreatedAt())
                .updatedAt(savedNotification.getUpdatedAt())
                .isDeleted(savedNotification.getIsDeleted())
                .totalReceivers(savedNotification.getTotalReceivers())
                .receiversId(receives.stream().map(NotificationReceiveEntity::getUserId).collect(Collectors.toList()))
                .build();
        // Send real-time WebSocket notification to each receiver
        notificationResponse.getReceiversId().forEach(userId -> {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + userId,
                    notification
            );
        });
        return notificationResponse;
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
        List<NotificationReceiveEntity> receives = new ArrayList<>();
        for (String userId : receiverIds) {
            String deliveryMethod = determineDeliveryMethod(userId, settings);
            if (deliveryMethod.equals("email")) {
                sendNotificationByEmail(notification, userId);
            } else {
                receives.add(NotificationReceiveEntity.builder()
                        .notificationId(notification.getId())
                        .userId(userId)
                        .build());
            }
        }
        return receives;
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
    public void updateUserNotificationSettings(UpdateUserNotificationSettingRequest request) {
        UserNotificationSettingsEntity settings = userNotificationSettingsRepository
                .findByUserIdAndNotificationSettingId(request.getUserId(), request.getNotificationSettingId())
                .orElse(UserNotificationSettingsEntity.builder()
                        .userId(request.getUserId())
                        .notificationSettingId(request.getNotificationSettingId())
                        .build());
        NotificationSettingsEntity notificationSettings = notificationSettingsRepository
                .findById(request.getNotificationSettingId()).orElseThrow();
        settings.setNotificationSettingName(notificationSettings.getNotificationType());
        settings.setEnabled(request.getEnabled());
        settings.setDeliveryMethod(request.getDeliveryMethod());
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

    @Override
    public List<NotificationSettingsEntity> searchNotificationSettings(String keyword) {
        return notificationSettingsRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<UserNotificationSettingsEntity> getUserNotificationSettings(String userId,String search) {
        return userNotificationSettingsRepository.findByUserIdAndNotificationNameRegex(userId,search);
    }

    // Helper method để validate frequency
    private void    validateNotificationFrequency(String authorId, NotificationSettingsEntity settings) {
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

    private void sendNotificationByEmail(NotificationEntity notification, String userId) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user == null) {
               return;
            }
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("body", notification.getMessage());
            context.setVariable("toMail", user.getEmail());
            String htmlContent = templateEngine.process("email-template", context);


            mimeMessageHelper.setFrom(mailFrom);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject(notification.getTitle());



            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException( "Error while sending email");
        }
    }

}