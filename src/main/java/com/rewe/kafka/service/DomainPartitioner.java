package com.rewe.kafka.service;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DomainPartitioner implements Partitioner, Deserializer<String> {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        System.out.println("start assign partition");
        if (key instanceof String) {
            switch ((String) key) {
                case "gmail":
                    return 0;
                case "yahoo":
                    return 1;
                case "rewe":
                    return 2;
            }
        }
        // Default partitioning
        return Math.abs(key.hashCode()) % cluster.partitionCountForTopic(topic);
    }

    private final Deserializer<String> delegate = new StringDeserializer();

    @Override
    public String deserialize(String topic, byte[] data) {
//        return new StringDeserializer().deserialize(topic, data);
        return new String(data);
    }

    @Override
    public void close() {
        System.out.println("close ");
    }

    @Override
    public void configure(Map<String, ?> map) {
        System.out.println("config ");
    }

//    @StreamListener(YourKafkaBindings.YOUR_INPUT_CHANNEL)
//    public void handleMessage(@Payload YourMessageType message, @Headers MessageHeaders headers) {
//
//        System.out.println( message );
//    }

}
