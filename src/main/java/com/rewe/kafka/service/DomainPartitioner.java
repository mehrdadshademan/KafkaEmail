package com.rewe.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DomainPartitioner implements Partitioner  {
    private final Map<String, Integer> keyToPartitionMap = new HashMap<>();

    public DomainPartitioner() {
        keyToPartitionMap.put("gmail", 0);
        keyToPartitionMap.put("yahoo", 1);
        keyToPartitionMap.put("rewe", 2);
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        log.debug("Assigning partition according to key: {}", key);
        Integer partition = keyToPartitionMap.get(key);
        if (partition != null) {
            return partition;
        }
        // Default partitioning
        return Math.abs(key.hashCode()) % cluster.partitionCountForTopic(topic);
    }

    @Override
    public void close() {
        
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
