package com.mine.socialmedia.exceptions;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException() {
        super("Passwords do not match");
    }
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
