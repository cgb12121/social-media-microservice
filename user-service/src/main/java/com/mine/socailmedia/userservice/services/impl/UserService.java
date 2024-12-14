package com.mine.socailmedia.userservice.services.impl;

import com.mine.socailmedia.userservice.entities.User;
import com.mine.socailmedia.userservice.repositories.UserRepository;
import com.mine.socailmedia.userservice.services.intf.IUserService;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<User> getUserById(UUID id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<User> getUserByAuthId(UUID authId) {
        return userRepository.findByAuthId(authId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    @Transactional
    public ResponseEntity<String> register(RegisterRequest registerRequest) {
        User user = new User(
                null,
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.address(),
                registerRequest.country(),
                registerRequest.authId()
        );
        userRepository.save(user);
        return ResponseEntity.ok().body("Register Success");
    }

    @Override
    public ResponseEntity<User> getUserInformation(UUID id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
