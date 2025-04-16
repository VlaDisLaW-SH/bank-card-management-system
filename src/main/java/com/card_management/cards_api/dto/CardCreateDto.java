package com.card_management.cards_api.dto;

import jakarta.validation.constraints.*;
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
    @Min(value = 1, message = "Месяц должен быть не менее 1")
    @Max(value = 12, message = "Месяц должен быть не более 12")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodMonth;

    /**
     * Год окончания действия карты
     */
    @NotNull
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodYear;

    /**
     * Статус карты
     */
    @NotNull
    @NotBlank
    private String status;

    /**
     * Баланс карты
     */
    @Positive(message = "Введите положительное значение")
    private Integer balance;
}
