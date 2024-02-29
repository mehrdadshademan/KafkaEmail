package com.rewe.kafka.service;

import com.rewe.kafka.domain.EmailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${Kafka.topic}")
     private String topic;

    public void send(String key, EmailModel data) {
        log.debug("send to kafka, topic:{}, key:{} , data:{}", topic, key, data); //for security reason we should not log the data but this project is sample
        kafkaTemplate.send(topic, key, data);
    }

}
