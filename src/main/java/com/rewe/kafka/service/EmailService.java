package com.rewe.kafka.service;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.enums.EmailDomainEnum;
import com.rewe.kafka.exceptions.EmailRandomException;
import com.rewe.kafka.exceptions.EmailRandomInvalidInputException;
import com.rewe.kafka.repository.EmailRepository;
import de.huxhorn.sulky.ulid.ULID;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final ULID ulid = new ULID();
    static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

    @Value("${email.content.length}")
    private int contentLength;

    @Value("${email.receiver}")
    private String receiverList;

    private final EmailProducer emailProducer;
    private final EmailRepository emailRepository;
    public List<EmailModel> retrievedConsumedEmail(String topic) {
        log.debug("Show the consumed emails");
        List<EmailModel> allByTopicAndLogStatus = new ArrayList<>();
        try {
            topicInputValidation(topic);
            allByTopicAndLogStatus = emailRepository.findAllByTopic(topic);
            return allByTopicAndLogStatus;
        } catch (Exception ex) {
            log.error("Can not retrieved data for topic:{}", topic);
            throw new EmailRandomException("Can not retrieved data for topic" + topic);
        }
    }

    private void topicInputValidation(String topic) {
        if (!isValidTopic(topic)) {
            log.error("The topic is not valid as input, topic:{}", topic);
            throw new EmailRandomInvalidInputException("The topic is not valid as input, It is null or empty");
        }
    }

    public EmailModel autoGenerateAndSendEmail(String topic) {
        topicInputValidation(topic);
        try {
            Random random = new Random();
            String randomDomain = createRandomDomain(random);
            EmailModel randomEmail = new EmailModel();
            randomEmail.setTopic(topic);
            randomEmail.setContent(createRandomContent(contentLength,random));
            randomEmail.setSender(createRandomEmail(randomDomain));
            randomEmail.setRecipients(receiverList);
            emailProducer.send(randomDomain, randomEmail);
            log.debug("The email:{}  with topic:{} and key:{} sent to broker", randomEmail, topic, randomDomain);
            return randomEmail;
        } catch (Exception e) {
            log.error("Can not create random email and send to topic:{}, error message:{}", topic, e.getMessage());
            throw new EmailRandomException("Can not create random email and send to topic:" + topic);
        }
    }


    private boolean isValidTopic(String topic) {
        return !StringUtils.isBlank(topic);
    }

    /**
     * create randome content for email that define length of content in application property
     *
     * @param lengthContent length of content
     * @return random content
     */
    private String createRandomContent(int lengthContent,Random random) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < lengthContent; i++) {
            int index = random.nextInt(CHARS.length());
            content.append(CHARS.charAt(index));
        }
        return content.toString();
    }

    private String createRandomDomain(Random random) {
        EmailDomainEnum[] domains = EmailDomainEnum.values();
        return domains[random.nextInt(domains.length)].getDomain();
    }

    private String createRandomEmail(String domain) {
        return "Email_Random" + ulid.nextULID() + "@" + domain;
    }
}
