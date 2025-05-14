package com.card_management.technical.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FormatDateValidator implements ConstraintValidator<ValidFormatDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
