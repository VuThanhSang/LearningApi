//package com.example.learning_api.kafka.listener;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.RetryableTopic;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.stereotype.Service;
//import static com.example.learning_api.kafka.KafkaConstant.*;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class TestKafkaListener {
//
//
//    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
//    @KafkaListener(topics = TEST_NOTIFICATION_TOPIC, groupId = TEST_NOTIFICATION_CONSUMER_GROUP_ID, id = TEST_NOTIFICATION_CONSUMER_GROUP_ID + "-1")
//    public void teacherNotificationInMidTest(String message) {
//        log.info("Listener 1 consume  {}", message);
//    }
//
//
//}
