package com.rewe.kafka.service;

import com.rewe.kafka.constants.PartitionConstants;
import com.rewe.kafka.domain.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class DomainPartitioner implements Partitioner {
    private final Map<String, Integer> keyToPartitionMap = new HashMap<>();

    public DomainPartitioner() {
        keyToPartitionMap.put(PartitionConstants.PARTITION_ZERO, 0);
        keyToPartitionMap.put(PartitionConstants.PARTITION_ONE, 1);
        keyToPartitionMap.put(PartitionConstants.PARTITION_TWO, 2);
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        log.debug("Assigning partition according to key: {}", key);
        try {
            EmailModel email = (EmailModel) value;
            if (!isValidEmail(email)) {
                log.error("Sender email is not valid, Email:{}", value);
            }
            String domain = getDomain(email.getSender());
            Integer partition = keyToPartitionMap.get(domain);
            if (partition != null) {
                return partition;
            }
        } catch (Exception ex) {
            log.error("Can not find partition for value:{}", value);
        }
        // Default partitioning
        return Math.abs(topic.hashCode()) % cluster.partitionCountForTopic(topic);
    }

    /**
     * separate the domain from sender email
     *
     * @param sender sender email
     * @return domain as key
     */
    private String getDomain(String sender) {
        Pattern pattern = Pattern.compile("@([a-zA-Z0-9.-]+)\\.");
        Matcher matcher = pattern.matcher(sender);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        } else {
            log.error("Sender email is not valid, email:{}", sender);
        }
        return null;
    }

    /**
     * validation for right sender email address
     *
     * @param data email data model
     * @return boolean that is email model valid or not
     */
    private boolean isValidEmail(EmailModel data) {
        return data != null && data.getSender() != null && data.getSender().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
