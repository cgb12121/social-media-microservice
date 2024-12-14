package com.mine.socialmedia.services.client;

import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import com.mine.socialmedia.config.UserServiceClientConfig;
import com.mine.socialmedia.services.fallback.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        configuration = UserServiceClientConfig.class,
        fallback = UserServiceFallback.class,
        url = "${feign.client.config.user-service.url}"
)
public interface UserServiceClient {

    @PostMapping("/users/register")
    @Transactional(rollbackFor = Exception.class)
    ResponseEntity<String> register(@RequestBody RegisterRequest request);

    default ResponseEntity<String> registerFallback(RegisterRequest request, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User registration service is unavailable.");
    }
}
