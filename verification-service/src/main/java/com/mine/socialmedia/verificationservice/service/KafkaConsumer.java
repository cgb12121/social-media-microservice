package com.mine.socialmedia.verificationservice.service;

import com.mine.socialmedia.verificationservice.dto.EmailDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class KafkaConsumer {

    private final OtpService otpService;

    private final UrlService urlService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "otp-topic", groupId = "verification-group")
    public void generateOtp(UUID userId, String userPhoneNumber) {
        String otp = otpService.generateOtp(userId);
        log.info("Generated OTP for {}: {}", userId, otp);
        kafkaTemplate.send("otp-topic", otp);
        otpService.sendOtp(userPhoneNumber, otp);
    }

    @KafkaListener(topics = "url-topic", groupId = "verification-group")
    public void generateUrl(UUID userId, String userEmail) {
        String url = urlService.generateUrl(userId);
        log.info("Generated URL for {}: {}", userId, url);
        kafkaTemplate.send("url-topic", url);
        urlService.sendEmail(
                EmailDetails.builder()
                        .receiver(userEmail)
                        .subject("Verify link")
                        .body(url)
                        .attachment(null)
                        .build()
        );
    }
}
