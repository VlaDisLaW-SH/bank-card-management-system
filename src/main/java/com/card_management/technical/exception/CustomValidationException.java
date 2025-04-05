package com.card_management.technical.exception;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class CustomValidationException extends RuntimeException {
    private final BindingResult bindingResult;

    public CustomValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public Map<String, String> getErrors() {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return errors;
    }
}
