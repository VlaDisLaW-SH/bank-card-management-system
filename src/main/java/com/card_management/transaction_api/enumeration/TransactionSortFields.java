package com.card_management.transaction_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для сортировки транзакции
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum TransactionSortFields {
    UUID("uuid"),
    USER_UUID("userUuid"),
    MASKED_SOURCE("maskedSource"),
    MASKED_DESTINATION("maskedDestination"),
    TRANSACTION_TYPE("transactionType"),
    AMOUNT("amount"),
    CREATED_AT("createdAt");

    private final String field;
}
