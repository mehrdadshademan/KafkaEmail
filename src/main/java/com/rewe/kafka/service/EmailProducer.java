package com.rewe.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class EmailProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EmailProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        send("my-topic", "yahoo", "T X");
        send("my-topic", "gmail", "TeSSxi XEX");
    }

    public void send(String topic, String key, Object data) {
        log.debug("send to kafka, topic:{}, key:{}", topic, key); //for security reason we should not log the data
        kafkaTemplate.send(topic, key, data);
    }

}
