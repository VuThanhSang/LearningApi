//package com.example.learning_api.kafka;
//
//
//import com.example.learning_api.kafka.message.NotificationMsgData;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.converter.JsonMessageConverter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConfig {
//
//
//    @Bean
//    public NewTopic createMailTopic() {
//        return new NewTopic(KafkaConstant.SEND_CODE_EMAIL_TOPIC, 2, (short) 1);
//    }
//
//    @Bean
//    public NewTopic testNotificationTopic() {
//        return new NewTopic(KafkaConstant.TEST_NOTIFICATION_TOPIC, 4, (short) 1);
//    }
//
//}