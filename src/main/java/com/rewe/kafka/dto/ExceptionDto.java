package com.rewe.kafka.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ExceptionDto {
    private String error;
    private String message;
    private int status;
    private LocalDateTime errorDate;

    public ExceptionDto(String errorMessage, HttpStatus status) {
        this.message = errorMessage;
        this.error = status.getReasonPhrase();
        this.status = status.value();
        this.errorDate = LocalDateTime.now();
    }
}
