package com.mine.socialmedia.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException( ) {
        super("User does not exist");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
