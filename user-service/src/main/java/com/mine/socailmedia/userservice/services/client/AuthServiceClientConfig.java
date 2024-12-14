package com.mine.socailmedia.userservice.services.client;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class AuthServiceClientConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("Content-Type", "application/json");
        };
    }

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            String jwtToken = "Bearer " + getJwtToken();
            requestTemplate.header("Authorization", jwtToken);
        };
    }

    private String getJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return "Bearer " + authentication.getDetails();
        }
        return null;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}