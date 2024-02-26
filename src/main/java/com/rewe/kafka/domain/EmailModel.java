package com.rewe.kafka.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmailModel implements Serializable {
    private String topic;
    private String content;
    private String sender ;
    private List<String> recipients ;
}
