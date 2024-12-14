package com.mine.socailmedia.userservice.services.client;

import com.mine.socailmedia.userservice.services.fallback.AuthServiceFallback;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        configuration = AuthServiceClientConfig.class,
        fallback = AuthServiceFallback.class,
        url = "${feign.client.config.auth-service.url}"
)
public interface AuthServiceClient {
    @PostMapping("/users/register")
    ResponseEntity<String> register(@RequestBody RegisterRequest request);

    default ResponseEntity<String> registerFallback(RegisterRequest request, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User registration service is unavailable.");
    }
}
