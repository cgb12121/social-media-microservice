package com.mine.socialmedia.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class KafkaPublisher {

    private final KafkaTemplate<UUID, String> kafkaTemplate;

    public void sendUrlRequest(UUID userId, String url) {
        kafkaTemplate.send("url-topic", userId, url);
    }

    public void sendOtpRequest(UUID userId, String otp) {
        kafkaTemplate.send("otp-topic", userId, otp);
    }
}
