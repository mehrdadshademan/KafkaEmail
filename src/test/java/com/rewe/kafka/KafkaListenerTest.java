package com.rewe.kafka;

import com.rewe.kafka.config.KafkaConfig;
import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.dto.EmailResponseDto;
import com.rewe.kafka.exceptions.EmailRandomInvalidInputException;
import com.rewe.kafka.repository.EmailRepository;
import com.rewe.kafka.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(KafkaConfig.class)
@SpringBootTest(classes = EmailMessengerApplication.class)
@Testcontainers
public class KafkaListenerTest {

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private EmailService service;


    @Autowired
    EmailRepository repository;


    @ParameterizedTest
    @ValueSource(strings = {"gmail", "yahoo", "rewe", "at"})
    void should_Success_When_PartitionNumberIsAccordingToKey(String key) throws ExecutionException, InterruptedException {
        EmailModel emailRequest = new EmailModel();
        String topic = "my-topic";
        emailRequest.setSender("test@" + key + ".com");
        emailRequest.setTopic("email-topic");
        emailRequest.setContent("messages mail");

        CompletableFuture<SendResult<String, Object>> result = kafkaTemplate.send(topic, emailRequest);
        SendResult<String, Object> expectedResult = result.get();
        switch (key) {
            case "gmail": {
                Assertions.assertEquals(0, expectedResult.getRecordMetadata().partition());
                break;
            }
            case "yahoo": {
                Assertions.assertEquals(1, expectedResult.getRecordMetadata().partition());
                break;
            }
            case "rewe": {
                Assertions.assertEquals(2, expectedResult.getRecordMetadata().partition());
                break;
            }
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void should_ThrowEmailRandomInvalidInputException_When_TopicInputIsWrong(String topic) {
        Assertions.assertThrows(EmailRandomInvalidInputException.class, () -> service.autoGenerateAndSendEmail(topic));
    }


    @Test
    void should_SuccessAndGenerateEmail_When_TopicInputIsRight() {
        String topic = "my-topic";
        EmailModel expectEmail = new EmailModel();
        expectEmail.setTopic(topic);
        EmailResponseDto emailModel = service.autoGenerateAndSendEmail(topic);
        Assertions.assertNotNull(emailModel);
        Assertions.assertEquals(expectEmail.getTopic(), emailModel.getTopic());
    }

    @Test
    void should_Success_When_SendCorrectEmailToKafkaAndConsumeIt() {
        EmailModel emailModel = new EmailModel();
        String domain = "gmail";
        emailModel.setSender("Tester@" + domain + ".com");
        emailModel.setTopic("Testing Kafka ");
        emailModel.setContent("content if from " + domain);
        emailModel.setRecipients("Test.Mehrdad@gmail.com");
        kafkaTemplate.send("my-topic", emailModel);
        await()
                .pollInterval(Duration.ofSeconds(2))
                .atMost(Duration.ofMinutes(2))
                .untilAsserted(() -> {
                    List<EmailModel> allEmails = repository.findAll();
                    assertNotEquals(0, allEmails.size());
                    EmailModel consumedEmail = allEmails.get(allEmails.size()-1);
                    assertEquals(emailModel.getSender(), consumedEmail.getSender());
                    assertEquals(emailModel.getTopic(), consumedEmail.getTopic());
                });
    }


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}