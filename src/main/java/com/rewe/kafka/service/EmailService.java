package com.rewe.kafka.service;

import com.rewe.kafka.enums.EmailDomainEnum;
import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.exceptions.EmailRandomException;
import com.rewe.kafka.exceptions.EmailRandomInvalidInputException;
import com.rewe.kafka.repository.EmailRepository;
import de.huxhorn.sulky.ulid.ULID;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final ULID ulid = new ULID();
    static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

    @Value("${email.content.length}")
    private int contentLength;

    @Value("${email.receiver}")
    private List<String> receiverList;

    private final EmailProducer emailProducer;
    private final EmailListener emailListener;
   // private final EmailRepository emailRepository;

    public List<EmailModel> consumeEmailsEveryDay() {
        log.debug("consume all of emails");
        return emailListener.getAllConsumeEmails();
    }

    public Set<EmailModel> consumeEmailsByRange(LocalDateTime startRange, LocalDateTime endRange) {
        log.debug("consume email by range of start:{} , end:{}", startRange, endRange);
        //todo validation for range
        return emailListener.getEmailsInTimeRange(startRange, endRange);
    }


    public EmailModel autoGenerateAndSendEmail(String topic) {
        if (!isValidTopic(topic)) {
            log.error("The topic is not valid as input, topic:{}", topic);
            throw new EmailRandomInvalidInputException("The topic is not valid as input, It is null or empty");
        }
        try {
            String randomDomain = createRandomDomain();
            EmailModel randomEmail = new EmailModel();
            randomEmail.setTopic(topic);
            randomEmail.setContent(createRandomContent(contentLength));
            randomEmail.setSender(createRandomEmail(randomDomain));
            randomEmail.setRecipients(receiverList);
            emailProducer.send(topic, randomDomain, randomEmail);
           // emailRepository.save(randomEmail);
            log.debug("the email send to broker");
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
    private String createRandomContent(int lengthContent) {
        Random random = new Random();
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < lengthContent; i++) {
            int index = random.nextInt(chars.length());
            content.append(chars.charAt(index));
        }
        return content.toString();
    }

    private String createRandomDomain() {
        EmailDomainEnum[] domains = EmailDomainEnum.values();
        return domains[new Random().nextInt(domains.length)].getDomain();
    }

    private String createRandomEmail(String domain) {
        return "Email_Random" + ulid.nextULID() + "@" + domain;
    }
}
