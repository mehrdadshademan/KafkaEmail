package com.rewe.kafka.service;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailListener {

    private final EmailRepository repository;
    /**
     * email listener, consume from kafka and store Async into the database
     * @param emailModelList list of email
     */
    @KafkaListener(topics =  "${kafka.topic}", groupId =  "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory",concurrency ="${kafka.concurrent.listener}")
    public void consume(List<EmailModel> emailModelList) {
        try {
            // Asynchronously save the list of emails to the database
            CompletableFuture.runAsync(() ->
            {
                repository.saveAll(emailModelList);
                log.debug("the Email listened and put into the DB");
            });
        } catch (Exception e) {
            log.error("The listener has error, error message:{}", e.getMessage());
        }
    }
}
