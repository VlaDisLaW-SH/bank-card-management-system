package com.card_management.transaction_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TransactionByCardDto {
    /**
     * UUID владельца карты
     */
    @NotNull
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Неверный формат UUID"
    )
    private String ownerUuid;

    /**
     * Последние 4 цифры номера карты
     */
    @NotNull
    @NotBlank
    @Size(min = 4, max = 4)
    private String cardLastFourDigits;
}
