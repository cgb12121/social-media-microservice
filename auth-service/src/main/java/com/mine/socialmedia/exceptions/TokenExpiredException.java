package com.mine.socialmedia.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token is expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
