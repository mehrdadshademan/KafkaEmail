package com.rewe.kafka;

import com.rewe.kafka.service.EmailListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;


@SpringBootTest(classes = TestApplication.class)
@EmbeddedKafka(partitions = 3, topics = "my-topic" )
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.group-id=my-group"
//                "spring.datasource.url=jdbc:tc:mysql:8.0.32:///db",
        }
)
@Testcontainers
class KafkaListenerTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    ).withEnv("KAFKA_NUM_PARTITIONS", "3");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String , String> kafkaListenerContainerFactory;


    @Autowired
    private EmailListener emailListener;

    @Value("${Kafka.topic}")
    private String topic;

    @BeforeEach
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }



    @Test
    void shouldHandleProductPriceChangedEvent() throws ExecutionException, InterruptedException {

        String topic = "my-topic";
        String key = "yahoo";
        String message = "mehrdad sendTEST";
        CompletableFuture<SendResult<String, String>> result = kafkaTemplate.send(topic, key, message);
        SendResult<String, String> expectedResult = result.get();
        ProducerRecord<String, String> producerRecord = expectedResult.getProducerRecord();

//
//        Properties consumerProps = new Properties();
//        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
//        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//
//        // Create a Kafka consumer
//        try (Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
//            // Subscribe to the topic
//            consumer.subscribe(Collections.singletonList("my-topic"));
//
//            // Send a message to each partition
//            for (int i = 0; i < 3; i++) {
//                kafkaTemplate.send("my-topic", String.valueOf(i), "mehrdad SDAASdsend");
//            }
//
//            // Consume messages from all partitions
//            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(50));
//
//            // Assert that messages were received from all three partitions
//            assertEquals(3, records.count());
//        }


    }

    /// yeki yahoo , gmail , rev baraye partition
    //test object hamono begirm

}
