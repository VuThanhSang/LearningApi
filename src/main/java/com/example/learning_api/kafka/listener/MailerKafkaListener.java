//package com.example.learning_api.kafka.listener;
//
//import com.example.learning_api.kafka.message.CodeEmailMsgData;
//import com.example.learning_api.kafka.message.UserBlockedMsgData;
//import com.example.learning_api.service.common.EmailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.RetryableTopic;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.stereotype.Service;
//
//import static com.example.learning_api.kafka.KafkaConstant.*;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class    MailerKafkaListener {
//    private final EmailService emailService;
//
//
//    // SEND_CODE_EMAIL_TOPIC
//    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
//    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC, groupId = SEND_EMAIL_CONSUMER_GROUP_ID, id = SEND_EMAIL_CONSUMER_GROUP_ID + "-1")
//    public void consumeSendCodeEmail1(CodeEmailMsgData message) {
//        log.info("Listener 1 consume  {}", message.toString());
//        emailService.sendHtmlVerifyCodeToRegister(message.getEmail(), message.getCode(),"Verify Code");
//    }
//
//    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
//    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC, groupId = SEND_EMAIL_CONSUMER_GROUP_ID, id = SEND_EMAIL_CONSUMER_GROUP_ID + "-2")
//    public void consumeSendCodeEmail2(CodeEmailMsgData message) {
//        log.info("Listener 2 consume  {}", message.toString());
//        emailService.sendHtmlVerifyCodeToRegister(message.getEmail(), message.getCode(),"Verify Code");
//    }
//
//    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC + "-dlt", groupId = SEND_EMAIL_CONSUMER_GROUP_ID, id = SEND_EMAIL_CONSUMER_GROUP_ID + "-dlt")
//    public void consumeSendCodeEmailDLT(CodeEmailMsgData message) {
//        log.error("Listener {} DLT consume  =>>> {}", SEND_EMAIL_CONSUMER_GROUP_ID, message.toString());
//    }
//
//    // SEND_USER_BLOCKED_EMAIL_TOPIC
//    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
//    @KafkaListener(topics = SEND_USER_BLOCKED_EMAIL_TOPIC, groupId = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID, id = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID + "-1")
//    public void consumeUserBlockedData1(UserBlockedMsgData message) {
//        log.info("Listener 1 consume {}", message.toString());
//        emailService.sendUserBlocked(message.getEmail(), message.getUserName());
//    }
//
//    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
//    @KafkaListener(topics = SEND_USER_BLOCKED_EMAIL_TOPIC, groupId = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID, id = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID + "-2")
//    public void consumeUserBlockedData2(UserBlockedMsgData message) {
//        log.info("Listener 2 consume {}", message.toString());
//        emailService.sendUserBlocked(message.getEmail(), message.getUserName());
//    }
//
//    @KafkaListener(topics = SEND_USER_BLOCKED_EMAIL_TOPIC + "-dlt", groupId = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID, id = SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID + "-dlt")
//    public void consumeUserBlockedDataDLT(UserBlockedMsgData message) {
//        log.error("Listener {} DLT consume =>>> {}", SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID, message.toString());
//    }
//}
