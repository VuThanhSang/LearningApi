//package com.example.learning_api.kafka;
//
//
//import com.example.learning_api.dto.common.NotificationEventDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationConsumerDispatcher {
//    private final List<com.example.learning_api.kafka.listener.KafkaListener<NotificationEventDTO>> listeners;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @KafkaListener(topics = "notification-#{'${notification.topics}'.split(',')}")
//    public void consumeNotification(NotificationEventDTO event) {
//        // Tìm listener phù hợp
//        listeners.stream()
//                .filter(listener -> listener.supports(event.getType()))
//                .findFirst()
//                .ifPresent(listener -> {
//                    // Thực thi listener
//                    listener.listen(event);
//
//                    // Gửi thông báo realtime
//                    broadcastNotification(event);
//                });
//    }
//
//    private void broadcastNotification(NotificationEventDTO event) {
//        // Gửi thông báo tới từng người nhận qua WebSocket
//        for (String receiverId : event.getReceiverIds()) {
//            messagingTemplate.convertAndSendToUser(
//                    receiverId,
//                    "/topic/notifications",
//                    event
//            );
//        }
//    }
//}