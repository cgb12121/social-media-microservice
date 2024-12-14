package com.mine.socialmedia.services.fallback;

import com.mine.socialmedia.services.client.VerificationServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VerificationServiceFallBack implements VerificationServiceClient {

    @Override
    public ResponseEntity<Boolean> validateOtp(UUID id, String otp) {
        throw new RuntimeException("OTP verification failed");
    }

    @Override
    public ResponseEntity<Boolean> verifyUrl(UUID userId, String url) {
        throw new RuntimeException("URL verification failed");
    }

    @Override
    public ResponseEntity<String> validateOtpFallback(UUID id, String otp, Throwable t) {
        return VerificationServiceClient.super.validateOtpFallback(id, otp, t);
    }

    @Override
    public ResponseEntity<String> validateUrlFallback(UUID id, String url, Throwable t) {
        return VerificationServiceClient.super.validateUrlFallback(id, url, t);
    }
}
