package com.card_management.technical.handler;

import com.card_management.cards_api.exception.BlockedCardException;
import com.card_management.cards_api.exception.DuplicateCardException;
import com.card_management.limits_api.exception.DuplicateLimitException;
import com.card_management.limits_api.exception.ExceedingLimitException;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.technical.exception.record.ErrorResponse;
import com.card_management.technical.exception.record.FieldErrorDto;
import com.card_management.transaction_api.exception.InsufficientFundsForTransactionException;
import com.card_management.users_api.exception.DuplicateEmailException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<FieldErrorDto> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return new FieldErrorDto(field, violation.getMessage());
                })
                .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExceedingLimitException.class)
    public ResponseEntity<ErrorResponse> handleExceedingLimitException(ExceedingLimitException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateLimitException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLimitException(DuplicateLimitException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientFundsForTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsForTransactionException(
            InsufficientFundsForTransactionException ex
    ) {
        return buildErrorResponse(ex, HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(BlockedCardException.class)
    public ResponseEntity<ErrorResponse> handleBlockedCardException(BlockedCardException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FieldsValidationException.class)
    public ResponseEntity<ErrorResponse> handleFieldsValidationException(FieldsValidationException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateCardException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCardException(DuplicateCardException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status) {
        List<FieldErrorDto> errors = List.of(new FieldErrorDto(null, ex.getMessage()));
        return ResponseEntity.status(status).body(new ErrorResponse(errors));
    }
}
