package com.globsest.testworkcreditcards.exceptions;

public class TransferException extends RuntimeException {
    private final String errorCode;

    public TransferException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}