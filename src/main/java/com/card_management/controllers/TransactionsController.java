package com.card_management.controllers;

import com.card_management.controllers.common.TransactionValidator;
import com.card_management.technical.exception.CustomValidationException;
import com.card_management.transaction_api.dto.*;
import com.card_management.transaction_api.service.TransactionService;
import com.card_management.users_api.model.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getTransactions(page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @GetMapping(path = "/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> getTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService
                .getUserTransactions(userDetails.getId(), page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDto> findById(@PathVariable Long id) {
        var transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransactionCreateDto transactionData,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        transactionValidator.validateCreateTransaction(transactionData, userDetails.getId());
        var transaction = transactionService.create(transactionData, userDetails.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@PathVariable Long id) {
        transactionService.delete(id);
    }

    @GetMapping(path = "/transactionsByUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> getCardTransactions(
            @Valid @RequestBody TransactionByCardDto transactionByCardDto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getCardTransactionsForAdmin(
                transactionByCardDto,
                page,
                size,
                sort
        );
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @GetMapping(path = "/my/byCard/{cardLastFourDigits}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> getUserTransactionsByCard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable
            @Pattern(regexp = "\\d{4}", message = "Введите последние четыре цифры карты.")
            String cardLastFourDigits,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService
                .getUserTransactionsByCard(userDetails.getId(), cardLastFourDigits, page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @PostMapping(path = "/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> filterTransactions(
            @Valid @RequestBody TransactionAdminFilterDto filterDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        transactionValidator.validateFilterTransaction(filterDto.getTransactionFilterDto());
        var transactions = transactionService.filterTransactionsForAdmin(filterDto);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping(path = "/my/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> filterUserTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransactionFilterDto filterDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
        transactionValidator.validateFilterTransaction(filterDto);
        var transactions = transactionService.filterTransactions(filterDto, userDetails.getId());
        return ResponseEntity.ok(transactions);
    }
}
