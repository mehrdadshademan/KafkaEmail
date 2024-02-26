package com.rewe.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    public EmailProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        send("my-topic", "yahoo","Teest3");
        send("my-topic", "gmail","Test1");
    }

    public void send(String topic, String key, String data) {
        System.out.printf(" send the message to kafka ");
        kafkaTemplate.send(topic, key, data);
    }

}
