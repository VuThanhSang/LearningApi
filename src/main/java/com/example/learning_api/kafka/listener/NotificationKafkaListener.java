//package com.example.learning_api.kafka.listener;
//
//import com.example.learning_api.dto.request.notification.SendNotificationRequest;
//import com.example.learning_api.kafka.KafkaConstant;
//import com.example.learning_api.kafka.message.NotificationMsgData;
//import com.example.learning_api.service.core.INotificationService;
//import com.example.learning_api.service.redis.UserTokenRedisService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.messaging.MessagingException;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class NotificationKafkaListener {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final UserTokenRedisService userTokenRedisService;
//    private final INotificationService notificationService;
//
//    @KafkaListener(topics = KafkaConstant.NOTIFICATION_TOPIC, groupId = KafkaConstant.NOTIFICATION_CONSUMER_GROUP_ID)
//    public void consumeNotification(NotificationMsgData message) {
//        log.info("Received notification: {}", message);
//
//        try {
//            String userStatus = userTokenRedisService.getUserStatus(message.getSenderId());
//            log.debug("User status for {}: {}", message.getSenderId(), userStatus);
//
//            if ("online".equals(userStatus)) {
//                sendNotificationToOnlineUser(message);
//            } else {
//                saveNotificationForOfflineUser(message);
//            }
//        } catch (Exception e) {
//            log.error("Error processing notification for user {}: {}", message.getSenderId(), e.getMessage(), e);
//        }
//    }
//
//    private void sendNotificationToOnlineUser(NotificationMsgData message) {
//        try {
//            messagingTemplate.convertAndSendToUser(
//                    message.getSenderId(),
//                    "/queue/notifications",
//                    message
//            );
//            log.info("Notification sent successfully to online user: {}", message.getSenderId());
//        } catch (MessagingException e) {
//            log.error("Failed to send notification to online user {}: {}", message.getSenderId(), e.getMessage(), e);
//            saveNotificationForOfflineUser(message);
//        }
//    }
//
//    private void saveNotificationForOfflineUser(NotificationMsgData message) {
//        try {
//            notificationService.saveNotification(message);
//            log.info("Notification saved for offline user: {}", message.getSenderId());
//        } catch (Exception e) {
//            log.error("Failed to save notification for offline user {}: {}", message.getSenderId(), e.getMessage(), e);
//        }
//    }
//}