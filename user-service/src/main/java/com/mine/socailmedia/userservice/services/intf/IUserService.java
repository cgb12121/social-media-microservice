package com.mine.socailmedia.userservice.services.intf;

import com.mine.socailmedia.userservice.entities.User;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface IUserService {
    ResponseEntity<User> getUserById(UUID id);

    ResponseEntity<User> getUserByAuthId(UUID authId);

    ResponseEntity<String> register(RegisterRequest registerRequest);

    ResponseEntity<User> getUserInformation(UUID id);
}
