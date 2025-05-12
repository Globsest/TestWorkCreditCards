package com.globsest.testworkcreditcards.exceptions;
public class UserOperationException extends RuntimeException {
    private final String errorCode;

    public UserOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}