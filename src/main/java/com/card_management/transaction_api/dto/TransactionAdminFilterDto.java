package com.card_management.transaction_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для фильтрации транзакций (используется администратором)")
public class TransactionAdminFilterDto {

    @Schema(description = "ID инициатора транзакции", example = "1001")
    private Long userId;

    @Schema(description = "Dto с данными для фильтрации транзакций")
    @Valid
    private TransactionFilterDto transactionFilterDto;
}
