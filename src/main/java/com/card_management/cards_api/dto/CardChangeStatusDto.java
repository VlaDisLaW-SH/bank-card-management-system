package com.card_management.cards_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CardChangeStatusDto {
    /**
     * ID владельца карты
     */
    @NotNull
    private Long ownerId;

    /**
     * Последние 4 цифры номера карты
     */
    @NotNull
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String lastFourDigitsCardNumber;

    /**
     * Статус карты
     */
    @NotNull
    @NotEmpty
    private String status;
}
