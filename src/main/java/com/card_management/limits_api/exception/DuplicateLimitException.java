package com.card_management.limits_api.exception;

public class DuplicateLimitException extends RuntimeException {
    public DuplicateLimitException(String message) {
        super(message);
    }
}
