package com.mine.socialmedia.services.impl;

import com.mine.socialmedia.common.dtos.request.LoginRequest;
import com.mine.socialmedia.common.dtos.request.RegisterRequest;
import com.mine.socialmedia.common.dtos.response.AuthResponse;
import com.mine.socialmedia.entities.CredentialsEnabled;
import com.mine.socialmedia.entities.CredentialsRole;
import com.mine.socialmedia.entities.UserCredentials;
import com.mine.socialmedia.entities.UserCredentialsStatus;
import com.mine.socialmedia.exceptions.InvalidPasswordException;
import com.mine.socialmedia.exceptions.PasswordsDoNotMatchException;
import com.mine.socialmedia.exceptions.UserAlreadyExistsException;
import com.mine.socialmedia.exceptions.UserNotFoundException;
import com.mine.socialmedia.repositories.AuthRepository;
import com.mine.socialmedia.services.KafkaPublisher;
import com.mine.socialmedia.services.intf.IAuthService;
import com.mine.socialmedia.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImp implements IAuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final KafkaPublisher kafkaPublisher;

    @Override
    public AuthResponse login(LoginRequest request) {
        String username = request.username();
        String password = request.password();

        UserCredentials user = authRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);

        boolean validPassword = passwordEncoder.matches(password, user.getPassword());

        if (!validPassword) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtUtil.generateToken(user);
        Claims claims = jwtUtil.extractAllClaims(accessToken);
        String refreshToken = jwtUtil.generateRefreshToken(claims, user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return new AuthResponse(tokens);
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        String email = registerRequest.email();
        String phoneNumber = registerRequest.phoneNumber();
        String password = registerRequest.password();
        String reEnterPassword = registerRequest.reEnterPassword();

        if (!password.equals(reEnterPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        UserCredentials user = authRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);
        saveUser(email, phoneNumber, hashedPassword);
        authRepository.findByEmail(email).ifPresent(savedUser ->
            kafkaPublisher.sendUrlRequest(savedUser.getUserId(), savedUser.getEmail())
        );
    }

    @Override
    @Transactional
    public boolean updateVerifiedCredentialsStatus(UUID userId) {
        return authRepository.findByUserId(userId)
                .map(auth -> {
                    auth.setUserCredentialsStatus(UserCredentialsStatus.ACTIVE);
                    authRepository.save(auth);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public UUID getAuthId(String email) {
        UserCredentials user = authRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user.getUserId();
    }

    private void saveUser(String email, String phoneNumber, String hashedPassword) {
        UserCredentials newUser = UserCredentials.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .password(hashedPassword)
                .userCredentialsRole(CredentialsRole.USER)
                .accountEnabled(CredentialsEnabled.PENDING)
                .userCredentialsStatus(UserCredentialsStatus.ACTIVE)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .lastLoggedIn(LocalDate.now())
                .build();
        authRepository.save(newUser);
    }
}
