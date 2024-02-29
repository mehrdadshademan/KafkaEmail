package com.rewe.kafka.domain;

import lombok.Data;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailModel implements Serializable {
    private Long id;
    private String topic;
    private String content;
    private String sender ;
    private List<String> recipients ;
    private LocalDateTime sendDate;


}
