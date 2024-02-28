package com.rewe.kafka.web.rest.controller;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/generate-email/{topic}")
    public ResponseEntity<EmailModel> generateEmail(@PathVariable String topic) {
        return new ResponseEntity<>(emailService.autoGenerateAndSendEmail(topic), HttpStatus.OK);
    }

    @GetMapping("/consume")
    public ResponseEntity<List<EmailModel>> consumeEmail(@PathVariable String topic) {
        return new ResponseEntity<>(emailService.consumeEmailsEveryDay(), HttpStatus.OK);
    }
    @GetMapping("/consume/byTimeRange")
    public ResponseEntity<Set<EmailModel>> consumeEmail(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return new ResponseEntity<>(emailService.consumeEmailsByRange(startTime,endTime), HttpStatus.OK);
    }

}
