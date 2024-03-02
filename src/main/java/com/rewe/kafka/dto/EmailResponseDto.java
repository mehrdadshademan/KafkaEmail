package com.rewe.kafka.dto;

import lombok.Data;

@Data
public class EmailResponseDto {
    private String topic;
    private String content;
    private String sender;
    private String recipients;
}
