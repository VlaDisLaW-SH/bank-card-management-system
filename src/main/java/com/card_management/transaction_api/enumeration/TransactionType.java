package com.card_management.transaction_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип транзакции
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true, description = "Тип транзакции")
public enum TransactionType {
    WITHDRAWALS("Снятие средств с карты"),
    TRANSFER("Перевод средств с карты");

    private final String description;
}
