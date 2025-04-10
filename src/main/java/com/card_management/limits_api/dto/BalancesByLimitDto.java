package com.card_management.limits_api.dto;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.transaction_api.enumeration.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class BalancesByLimitDto {
    /**
     * UUID лимита
     */
    private UUID uuid;

    /**
     * UUID пользователя для которого установлен лимит
     */
    private UUID userUuid;

    /**
     * Тип лимита
     */
    private LimitType limitType;

    /**
     * Тип транзакции
     */
    private TransactionType transactionType;

    /**
     * Сумма остатков по лимиту
     */
    private Integer balances;
}
