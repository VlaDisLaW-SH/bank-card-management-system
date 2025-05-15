package com.card_management.transaction_api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TransactionByCardDto {
    /**
     * ID владельца карты
     */
    @NotNull
    @Positive
    private Long userId;

    /**
     * Последние 4 цифры номера карты
     */
    @NotNull
    @NotBlank
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String cardLastFourDigits;
}
