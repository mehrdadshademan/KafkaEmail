package com.rewe.kafka.exceptions;

public class EmailRandomInvalidInputException extends RuntimeException{
    public EmailRandomInvalidInputException(String message) {
        super(message);
    }
}
