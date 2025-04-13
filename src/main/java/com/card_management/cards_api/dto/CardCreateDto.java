package com.card_management.cards_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

@Setter
@Getter
@NoArgsConstructor
public class CardCreateDto {
    /**
     * Номер карты 16 цифр
     */
    @NotNull
    @NotBlank
    @Size(min = 16, max = 16)
    @CreditCardNumber
    private String cardNumber;

    /**
     * ID владельца карты
     */
    private Long ownerId;

    /**
     * Месяц окончания действия карты
     */
    @NotNull
    private Integer validityPeriodMonth;

    /**
     * Год окончания действия карты
     */
    @NotNull
    private Integer validityPeriodYear;

    /**
     * Статус карты
     */
    @NotNull
    @NotBlank
    private String status;

    private Integer balance;
}
