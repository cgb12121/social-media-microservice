package com.mine.socialmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        byte[] seed = "my-fixed-secret-seed".getBytes();
        SecureRandom secureRandom = new SecureRandom(seed);
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 12, secureRandom);
    }
}