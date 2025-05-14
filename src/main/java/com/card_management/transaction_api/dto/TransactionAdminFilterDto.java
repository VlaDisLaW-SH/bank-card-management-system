package com.card_management.transaction_api.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TransactionAdminFilterDto {
    /**
     * ID инициатора транзакции
     */
    private Long userId;

    /**
     * Dto с данными для фильтрации транзакций
     */
    @Valid
    private TransactionFilterDto transactionFilterDto;
}
