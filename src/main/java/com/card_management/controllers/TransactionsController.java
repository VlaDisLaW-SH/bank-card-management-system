package com.card_management.controllers;

import com.card_management.controllers.common.TransactionValidator;
import com.card_management.technical.exception.CustomValidationException;
import com.card_management.transaction_api.dto.*;
import com.card_management.transaction_api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    private final TransactionValidator transactionValidator;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionEnvelopDto> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getTransactions(page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionDto> findById(@PathVariable Long id) {
        var transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionDto> create(
            @Valid @RequestBody TransactionCreateDto transactionData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        transactionValidator.validateCreateTransaction(transactionData);
        var transaction = transactionService.create(transactionData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable Long id) {
        transactionService.delete(id);
    }

    @GetMapping(path = "/transactionsByUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionEnvelopDto> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getUserTransactions(userId, page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @PostMapping(path = "/transactionsByCard")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionEnvelopDto> getCardTransactions(
            @Valid @RequestBody TransactionByCardDto dto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getCardTransactions(dto, page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @PostMapping(path = "/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionEnvelopDto> filterTransactions(
            @Valid @RequestBody TransactionFilterDto filterDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        transactionValidator.validateFilterTransaction(filterDto);
        var transactions = transactionService.getFilteredTransactions(filterDto);
        return ResponseEntity.ok(transactions);
    }
}
