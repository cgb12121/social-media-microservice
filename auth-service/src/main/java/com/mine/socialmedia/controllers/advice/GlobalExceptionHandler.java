package com.mine.socialmedia.controllers.advice;

import com.mine.socialmedia.exceptions.*;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeoutException(TimeoutException e) {
        return ResponseEntity.status(408).body(getErrorResponse(e, getCurrentDateTime(), "Timeout"));
    }


    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleTokenExpiredException(TokenExpiredException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Token expired"));
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Map<String, String>> handleTokenException(TokenException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Something went wrong with token"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Invalid token"));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity.status(409).body(getErrorResponse(e, getCurrentDateTime(), "User already exists"));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordException(InvalidPasswordException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Wrong password or username"));
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<Map<String, String>> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException e) {
        return ResponseEntity.status(400).body(getErrorResponse(e, getCurrentDateTime(), "Passwords do not match"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(404).body(getErrorResponse(e, getCurrentDateTime(), "User not found"));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Map<String, String>> handleRateLimitException(RequestNotPermitted e) {
        return ResponseEntity.status(429).body(getErrorResponse(e, getCurrentDateTime(), "Too many requests"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, String>> handleExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Expired token"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException e) {
        return ResponseEntity.status(401).body(getErrorResponse(e, getCurrentDateTime(), "Unauthorized"));
    }

    private Map<String, String> getErrorResponse(Exception e, String errorTime, String errorMsg) {
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", errorTime);
        response.put("error", errorMsg);
        response.put("message", e.getMessage());
        return response;
    }

    private String getCurrentDateTime() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        ZoneId defaultZoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(defaultZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return zonedDateTime.format(formatter);
    }
}
