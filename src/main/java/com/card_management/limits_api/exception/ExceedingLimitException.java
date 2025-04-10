package com.card_management.limits_api.exception;

public class ExceedingLimitException extends RuntimeException {
    public ExceedingLimitException(String message) {
        super(message);
    }
}
