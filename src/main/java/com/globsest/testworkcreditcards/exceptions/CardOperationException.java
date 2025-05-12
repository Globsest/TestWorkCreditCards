package com.globsest.testworkcreditcards.exceptions;

public class CardOperationException extends RuntimeException {
    private final String errorCode;

    public CardOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
