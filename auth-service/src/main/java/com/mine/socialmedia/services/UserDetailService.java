package com.mine.socialmedia.services;

import com.mine.socialmedia.entities.UserCredentials;
import com.mine.socialmedia.repositories.AuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserCredentials user = authRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException(userId));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    @SuppressWarnings("unused")
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        UserCredentials user = authRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException(id.toString()));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
