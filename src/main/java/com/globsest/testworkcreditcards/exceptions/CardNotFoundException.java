package com.globsest.testworkcreditcards.exceptions;

public class CardNotFoundException extends CardOperationException {
    public CardNotFoundException(String message) {
        super(message, "Card not found");
    }
}