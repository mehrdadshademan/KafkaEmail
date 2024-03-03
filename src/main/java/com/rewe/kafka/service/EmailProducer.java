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

    @Value("${kafka.topic}")
    private String topic;

    /**
     * send email request as message to the broker "KAFKA"
     *
     * @param email email model that will send
     */
    public void sendEmail(EmailModel email) {
        log.debug("send to kafka, topic:{}, data:{}", topic, email); //for security reason we should not log the data but this project is sample
        kafkaTemplate.send(topic, email);
    }
}
