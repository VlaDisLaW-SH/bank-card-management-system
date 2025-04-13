package com.card_management.transaction_api.exception;

public class InsufficientFundsForTransactionException extends RuntimeException {
    public InsufficientFundsForTransactionException(String message) {
        super(message);
    }
}
