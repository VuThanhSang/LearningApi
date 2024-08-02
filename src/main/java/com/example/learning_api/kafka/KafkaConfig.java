package com.example.learning_api.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfig {


    @Bean
    public NewTopic createMailTopic() {
        return new NewTopic(KafkaConstant.SEND_CODE_EMAIL_TOPIC, 2, (short) 1);
    }

}