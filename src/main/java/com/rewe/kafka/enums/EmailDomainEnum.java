package com.rewe.kafka.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailDomainEnum {
    GMAIL_COM("gmail.com"),
    YAHOO_COM("yahoo.com"),
    REWE_AT("rewe.at"),
    TEST_COM("test.com");

    private final String domain;
}
