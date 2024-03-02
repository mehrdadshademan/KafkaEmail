package com.rewe.kafka.service;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.dto.EmailResponseDto;
import com.rewe.kafka.enums.EmailDomainEnum;
import com.rewe.kafka.exceptions.EmailRandomException;
import com.rewe.kafka.exceptions.EmailRandomInvalidInputException;
import com.rewe.kafka.mapper.EmailMapper;
import com.rewe.kafka.repository.EmailRepository;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private  final EmailMapper mapper;
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
        try {
            topicInputValidation(topic);
            return emailRepository.findAllByTopic(topic);
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

    /**
     * Generate random email with random content and send to the Kafka
     *
     * @param topic topic of email
     * @return return email random dto to show to user
     */
    public EmailResponseDto autoGenerateAndSendEmail(String topic) {
        topicInputValidation(topic);
        try {
            Random random = new Random();
            String randomDomain = createRandomDomain(random);
            EmailModel randomEmail = new EmailModel();
            randomEmail.setTopic(topic);
            randomEmail.setContent(createRandomContent(contentLength, random));
            randomEmail.setSender(createRandomEmail(randomDomain));
            randomEmail.setRecipients(receiverList);
            emailProducer.sendEmail(randomEmail);
            log.debug("The email:{}  with topic:{} and key:{} sent to broker", randomEmail, topic, randomDomain);
            return mapper.toDto(randomEmail);
         } catch (Exception e) {
            log.error("Can not create random email and send to topic:{}, error message:{}", topic, e.getMessage());
            throw new EmailRandomException("Can not create random email and send to topic:" + topic);
        }
    }

    /**
     * check validation of topic not null or empty
     * @param topic topic
     * @return the topic is valid or not
     */
    private boolean isValidTopic(String topic) {
        return !StringUtils.isBlank(topic);
    }

    /**
     * create randome content for email that define length of content in application property
     *
     * @param lengthContent length of content
     * @return random content
     */
    private String createRandomContent(int lengthContent, Random random) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < lengthContent; i++) {
            int index = random.nextInt(CHARS.length());
            content.append(CHARS.charAt(index));
        }
        return content.toString();
    }

    /**
     * Create random content
     * @param random random object
     * @return random domain between domain enum
     */
    private String createRandomDomain(Random random) {
        EmailDomainEnum[] domains = EmailDomainEnum.values();
        return domains[random.nextInt(domains.length)].getDomain();
    }

    /**
     *  Create random email address
     * @param domain domain of email like gmail/yahoo
     * @return random email
     */
    private String createRandomEmail(String domain) {
        return "Email_Random" + ulid.nextULID() + "@" + domain;
    }
}
