package com.mine.socialmedia.verificationservice.controller;

import com.mine.socialmedia.verificationservice.service.OtpService;
import com.mine.socialmedia.verificationservice.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/verify")
public class VerificationController {

    private final OtpService otpService;

    private final UrlService urlService;

    @PostMapping("/otp")
    public ResponseEntity<Boolean> validateOtp(@RequestParam UUID userId, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(userId, otp);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/url")
    public ResponseEntity<Boolean> validateUrl(@RequestParam UUID userId, @RequestParam String url) {
        boolean isUrlValid = urlService.verifyUrl(userId, url);
        return ResponseEntity.ok(isUrlValid);
    }
}
