//package com.example.learning_api.kafka.publisher;
//
//import com.example.learning_api.dto.common.NotificationEventDTO;
//import com.example.learning_api.enums.NotificationFormType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationKafkaPublisher implements KafkaPublisher {
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Override
//    public void publish(NotificationEventDTO event) {
//        String topic = "notification-" + event.getType().name().toLowerCase();
//        kafkaTemplate.send(topic, event);
//    }
//
//    @Override
//    public void publish(NotificationFormType type, String message) {
//        NotificationEventDTO event = NotificationEventDTO.builder()
//                .id(UUID.randomUUID().toString())
//                .type(type)
//                .message(message)
//                .title(type.name())
//                .build();
//        publish(event);
//    }
//
//    @Override
//    public void publish(NotificationFormType type, String title, String message) {
//        NotificationEventDTO event = NotificationEventDTO.builder()
//                .id(UUID.randomUUID().toString())
//                .type(type)
//                .title(title)
//                .message(message)
//                .build();
//        publish(event);
//    }
//}