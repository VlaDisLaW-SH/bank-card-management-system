package com.card_management.transaction_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TransactionEnvelopDto {
    /**
     * Список транзакций
     */
    private List<TransactionDto> transactions;

    /**
     * Кол-во элементов
     */
    private long totalElements;

    /**
     * Кол-во страниц
     */
    private int totalPages;
}
