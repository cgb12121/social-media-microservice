package com.mine.socialmedia.verificationservice.service;

import com.mine.socialmedia.verificationservice.dto.EmailDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@RequiredArgsConstructor
public class UrlService {

    private final JavaMailSender javaMailSender;

    //TODO: hashing before storing
    private final RedisTemplate<UUID, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${feign.client.config.verification-service.url}")
    private String domain;

    //TODO: consider async
    public void sendEmail(EmailDetails emailDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(emailDetails.getReceiver());
        message.setSubject(emailDetails.getSubject());
        message.setText(emailDetails.getBody());
        javaMailSender.send(message);
    }

    public String generateUrl(UUID userId) {
        StringBuilder verifyLink = new StringBuilder();
        verifyLink.append(domain);
        verifyLink.append(RandomStringUtils.randomAlphanumeric(30));
        redisTemplate.opsForValue().set(userId, String.valueOf(verifyLink), 15, TimeUnit.MINUTES);
        return String.valueOf(verifyLink);
    }

    public boolean verifyUrl(UUID userId, String verifyLink) {
        String storedUrl = redisTemplate.opsForValue().get(userId);
        if (storedUrl != null && storedUrl.equals(verifyLink)) {
            redisTemplate.delete(userId);
            return true;
        }
        return false;
    }
}
