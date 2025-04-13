package com.card_management.transaction_api.dto;

import com.card_management.transaction_api.enumeration.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class TransactionDto {
    /**
     * UUID транзакции
     */
    private UUID uuid;

    /**
     * UUID инициатора транзакции
     */
    private UUID userUuid;

    /**
     * Замаскированный номер карты в формате 0000****0000, используемой в качестве источника транзакции
     */
    private String maskedSource;

    /**
     * Замаскированный номер карты в формате 0000****0000, используемой в качестве получателя средств или цели операции
     */
    private String maskedDestination;

    /**
     * Тип транзакции
     */
    private TransactionType transactionType;

    /**
     * Сумма транзакции
     */
    private Integer amount;

    /**
     * Дата и время создания транзакции
     */
    private LocalDateTime createdAt;
}
