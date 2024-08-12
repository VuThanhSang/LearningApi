package com.example.learning_api.kafka.publisher;

import com.example.learning_api.kafka.KafkaConstant;
import com.example.learning_api.kafka.message.NotificationMsgData;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class NotificationKafkaPublisher {
    @Autowired
    private KafkaTemplate<String, Object> template;

    @Autowired
    private MeterRegistry meterRegistry;

    public void sendNotification(NotificationMsgData message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.NOTIFICATION_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            sample.stop(meterRegistry.timer("kafka.producer.latency", "topic", KafkaConstant.NOTIFICATION_TOPIC));
            if (ex == null) {
                meterRegistry.counter("kafka.producer.success", "topic", KafkaConstant.NOTIFICATION_TOPIC).increment();
                log.info("Publisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                meterRegistry.counter("kafka.producer.error", "topic", KafkaConstant.NOTIFICATION_TOPIC).increment();
                log.error("Publisher {} error {}", KafkaConstant.NOTIFICATION_TOPIC, ex.getMessage());
            }
        });
    }
}
