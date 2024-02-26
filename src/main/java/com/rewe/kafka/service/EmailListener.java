package com.rewe.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewe.kafka.domain.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class EmailListener {
    @Value("${Kafka.topic}")
    private String topic;

    @Value("${Kafka.group}")
    private String groupId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    DomainPartitioner domainPartitioner;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String key, String payload) {
        log.info("sending payload='{}' to topic='{} by key:{}'", payload, topic, key);
        kafkaTemplate.send(topic, key, payload);
    }

    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void consume(ConsumerRecord<String, String> data) throws JsonProcessingException {
        try {
            EmailModel email = objectMapper.readValue(data.value(), EmailModel.class);

        }catch (Exception e){
            System.out.println(data);
        }
    }

    public void checkConnection() {
        try {
            // Send a test message to a known topic
            kafkaTemplate.send("my-topic", "Checking Kafka Connection");
            System.out.println("Successfully connected to Kafka!");
        } catch (Exception e) {
            System.err.println("Failed to connect to Kafka. Error: " + e.getMessage());
        }
    }
}
