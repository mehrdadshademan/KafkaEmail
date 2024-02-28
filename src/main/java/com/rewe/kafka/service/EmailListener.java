package com.rewe.kafka.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewe.kafka.domain.EmailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailListener {
    @Value("${Kafka.topic}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ConcurrentHashMap<LocalDateTime, EmailModel> emailList = new ConcurrentHashMap<LocalDateTime, EmailModel>();


    @KafkaListener(topics = "my-topic", groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, Object> data) {
        try {
            EmailModel email = objectMapper.readValue((JsonParser) data.value(), EmailModel.class);
            emailList.put(LocalDateTime.now(), email);
            //java jpa repo
            System.out.println("XXX : Email");
        } catch (Exception e) {
            System.out.println("XXX : recive more thing ");
            System.out.println(data);
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
