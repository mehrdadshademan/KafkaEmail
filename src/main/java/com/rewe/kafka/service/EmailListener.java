package com.rewe.kafka.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.exceptions.EmailRandomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailListener {


    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private final ConsumerFactory<String, Object> consumerFactory;

    private final ConcurrentHashMap<LocalDateTime, EmailModel> emailList = new ConcurrentHashMap<LocalDateTime, EmailModel>();


    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = KafkaConstants.KAFKA_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void consume(EmailModel data) {
        try {
            EmailModel email = data;
            emailList.put(LocalDateTime.now(), email);
            log.debug("the Email listened and put into the local cach");
        } catch (Exception e) {
            log.error("The listener has error, error message:{}", e.getMessage());
        }
    }

    public void getConsumeByTopic(String topic, int duration) {
//        try (Consumer<String, GenericRecord> consumer = new KafkaConsumer<>(consumerFactoryConfig);) {
//todo fix this
        try (Consumer<String, Object> consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(duration));
            if (records != null && !records.isEmpty()) {
                for (ConsumerRecord<String, Object> record : records) {
                    Object value = record.value();
                    System.out.println("Received: " + value);
                }
            }
        } catch (Exception ex) {
            log.error("Can not consume topic:{} for duration:{} ", topic, duration);
            throw new EmailRandomException("Can not consume topic: " + topic + " for duration:" + duration);
        }
    }

    public Set<EmailModel> getEmailsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        Set<EmailModel> filterEmails = new HashSet<>();
        for (Map.Entry<LocalDateTime, EmailModel> entry : emailList.entrySet()) {
            LocalDateTime emailTimestamp = entry.getKey();
            if (emailTimestamp.isAfter(startTime) && emailTimestamp.isBefore(endTime)) {
                filterEmails.add(entry.getValue());
            }
        }
        return filterEmails;
    }

    public List<EmailModel> getAllConsumeEmails() {
        return new ArrayList<>(emailList.values());
    }

    /**
     * this method remove the hashmap data every day
     * scheduler run every 2 day at midnight
     */
    @Scheduled(cron = "0 0 0 */2 * ?")
    private void cleanUpOldData() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1);
        Iterator<Map.Entry<LocalDateTime, EmailModel>> iterator = emailList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LocalDateTime, EmailModel> entry = iterator.next();
            if (entry.getKey().isBefore(cutoffTime)) {
                iterator.remove();
            }
        }
    }


}
