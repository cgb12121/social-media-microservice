package com.mine.socialmedia.services.intf;


import com.mine.socialmedia.common.dtos.request.LoginRequest;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import com.mine.socialmedia.common.dtos.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface IAuthService {
    AuthResponse login(LoginRequest request);

    @Transactional
    void register(RegisterRequest registerRequest);

    @Transactional
    boolean updateVerifiedCredentialsStatus(UUID userId);

    UUID getAuthId(String email);
}
