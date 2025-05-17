package com.card_management.transaction_api.enumeration;

import com.card_management.technical.enumeration.FieldEnumerable;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для сортировки транзакции
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Допустимые значения для сортировки транзакции")
public enum TransactionSortFields implements FieldEnumerable {
    ID("id"),
    UUID("uuid"),
    USER_UUID("userUuid"),
    MASKED_SOURCE("maskedSource"),
    MASKED_DESTINATION("maskedDestination"),
    TRANSACTION_TYPE("transactionType"),
    AMOUNT("amount"),
    CREATED_AT("createdAt");

    private final String field;

    @Override
    @JsonValue
    public String toString() {
        return field;
    }
}
