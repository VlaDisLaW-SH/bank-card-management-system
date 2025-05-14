package com.card_management.technical.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FormatDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFormatDate {
    String message() default "Некорректный формат даты. Ожидается yyyy-MM-dd.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
