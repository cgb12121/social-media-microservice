package com.mine.socialmedia.services.client;

import com.mine.socialmedia.config.UserServiceClientConfig;
import com.mine.socialmedia.services.fallback.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "verification-service",
        configuration = UserServiceClientConfig.class,
        fallback = UserServiceFallback.class,
        url = "${feign.client.config.verification-service.url}"
)
public interface VerificationServiceClient {

    @PostMapping("/verify/otp")
    ResponseEntity<Boolean> validateOtp(
            @RequestParam("userId") UUID id,
            @RequestParam("otp") String otp
    );

    @PostMapping("/verify/url")
    ResponseEntity<Boolean> verifyUrl(
            @RequestParam UUID userId,
            @RequestParam String url
    );

    default ResponseEntity<String> validateOtpFallback(UUID id, String otp, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Verification service is unavailable.");
    }

    default ResponseEntity<String> validateUrlFallback(UUID id, String url, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Verification service is unavailable.");
    }
}
