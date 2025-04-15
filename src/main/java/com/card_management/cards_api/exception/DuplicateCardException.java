package com.card_management.cards_api.exception;

public class DuplicateCardException extends RuntimeException {
    public DuplicateCardException(String message) {
        super(message);
    }
}
