package com.card_management.limits_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для сортировки лимитов
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum LimitSortFields {
    ID("id"),
    UUID("uuid"),
    USER_UUID("userUuid"),
    LIMIT_TYPE("limitType"),
    TRANSACTION_TYPE("transactionType"),
    LIMIT_AMOUNT("limitAmount"),
    BALANCES("balances"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;
}
