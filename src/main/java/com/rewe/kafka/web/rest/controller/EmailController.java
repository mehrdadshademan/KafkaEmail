package com.rewe.kafka.web.rest.controller;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/consume/{topic}")
    public ResponseEntity<List<EmailModel>> retrievedConsumedEmail(@PathVariable String topic) {
        return new ResponseEntity<>(emailService.retrievedConsumedEmail(topic), HttpStatus.OK);
    }

}
