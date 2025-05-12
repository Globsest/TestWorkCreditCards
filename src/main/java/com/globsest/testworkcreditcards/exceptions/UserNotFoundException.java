package com.globsest.testworkcreditcards.exceptions;

public class UserNotFoundException extends CardOperationException {
    public UserNotFoundException(String message) {
        super(message, "User not found");
    }
}
