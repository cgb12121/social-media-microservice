package com.mine.socialmedia.controllers;

import com.mine.socialmedia.common.dtos.request.LoginRequest;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import com.mine.socialmedia.common.dtos.response.AuthResponse;
import com.mine.socialmedia.services.client.UserServiceClient;
import com.mine.socialmedia.services.client.VerificationServiceClient;
import com.mine.socialmedia.services.intf.IAuthService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
class AuthController {

    private final IAuthService authService;

    private final UserServiceClient userServiceClient;

    private final VerificationServiceClient verificationServiceClient;

    @Retry(name = "auth-retry")
    @TimeLimiter(name = "auth-timeLimiter")
    @Bulkhead(name="auth-bulkhead")
    @CircuitBreaker(name = "auth-breaker")
    @RateLimiter(name = "auth-rateLimiter")
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(authResponse);
        }).orTimeout(5, TimeUnit.SECONDS);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        UUID authId = authService.getAuthId(request.email());
        RegisterRequest registerRequest = new RegisterRequest(
                request.email(), request.phoneNumber(), request.password(), request.reEnterPassword(),
                request.firstName(), request.lastName(), request.address(), request.country(), authId
        );
        return userServiceClient.register(registerRequest);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(
            @RequestParam UUID id,
            @RequestParam String otp
    ) {
        ResponseEntity<Boolean> response = verificationServiceClient.validateOtp(id, otp);
        if (Boolean.FALSE.equals(response.getBody())) {
            return ResponseEntity.badRequest().body("OTP validation failed!");
        }
        boolean updated = authService.updateVerifiedCredentialsStatus(id);
        if (Boolean.FALSE.equals(updated)) {
            return ResponseEntity.badRequest().body("OTP validation failed!");
        }
        return ResponseEntity.ok("OTP validation successful!");
    }

    @PostMapping("/validate-url")
    public ResponseEntity<String> validateUrl(
            @RequestParam UUID id,
            @RequestParam String url
    ) {
        ResponseEntity<Boolean> response = verificationServiceClient.verifyUrl(id, url);
        if (Boolean.FALSE.equals(response.getBody())) {
            return ResponseEntity.badRequest().body("Url validation failed!");
        }
        boolean updated = authService.updateVerifiedCredentialsStatus(id);
        if (Boolean.FALSE.equals(updated)) {
            return ResponseEntity.badRequest().body("Url validation failed!");
        }
        return ResponseEntity.ok("Url validation successful!");
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}
