package com.globsest.testworkcreditcards.exceptions;

public class CardCreationException extends CardOperationException {
    public CardCreationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
