package com.mine.socailmedia.userservice.services.fallback;

import com.mine.socailmedia.userservice.services.client.AuthServiceClient;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFallback implements AuthServiceClient {

    @Override
    public ResponseEntity<String> register(RegisterRequest request) {
        throw new RuntimeException("Something went wrong");
    }
}
