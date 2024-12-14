package com.mine.socialmedia.services.fallback;

import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import com.mine.socialmedia.services.client.UserServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public ResponseEntity<String> registerFallback(RegisterRequest request, Throwable t) {
        return UserServiceClient.super.registerFallback(request, t);
    }

    @Override
    public ResponseEntity<String> register(RegisterRequest request) {
        throw new RuntimeException("Something went wrong");
    }
}
