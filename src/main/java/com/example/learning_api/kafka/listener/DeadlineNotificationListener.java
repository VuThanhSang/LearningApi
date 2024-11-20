//package com.example.learning_api.kafka.listener;
//
//import com.example.learning_api.dto.common.NotificationEventDTO;
//import com.example.learning_api.enums.NotificationFormType;
//
//import javax.management.Notification;
//
//public class DeadlineNotificationListener implements KafkaListener<NotificationEventDTO> {
//
//
//    @Override
//    public void listen(NotificationEventDTO event) {
//
//    }
//
//    @Override
//    public boolean supports(NotificationFormType type) {
//        return type == NotificationFormType.DEADLINE_NEW || type == NotificationFormType.DEADLINE_GRADED || type == NotificationFormType.DEADLINE_DUE_SOON || type == NotificationFormType.DEADLINE_OVERDUE ;
//    }
//}
