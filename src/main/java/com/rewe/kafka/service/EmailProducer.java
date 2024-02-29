package com.rewe.kafka.service;

import com.rewe.kafka.domain.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j

public class EmailProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private    ConsumerFactory<String, Object> consumerFactory;

    public EmailProducer(KafkaTemplate<String, Object> kafkaTemplate ,  ConsumerFactory<String, Object> consumerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory=consumerFactory;
//        send("my-topic", "yahoo", "T X");
        EmailModel emailModel = new EmailModel();
        emailModel.setSender("mehrdad@gmail.com");
        emailModel.setTopic("my-topic");
        emailModel.setContent("djakdklajdlkajkladd");
        send("my-topic", "gmail",  emailModel);
    }

    public void send(String topic, String key, EmailModel data) {
        log.debug("send to kafka, topic:{}, key:{}", topic, key); //for security reason we should not log the data
        kafkaTemplate.send(topic, key, data);
    }

}
