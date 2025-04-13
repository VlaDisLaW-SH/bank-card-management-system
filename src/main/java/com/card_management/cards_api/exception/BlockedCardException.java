package com.card_management.cards_api.exception;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException(String message) {
        super(message);
    }
}
