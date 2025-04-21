package com.card_management.transaction_api.dto;

import com.card_management.technical.validation.NullForNonAdmin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TransactionCreateDto {
    /**
     * ID инициатора транзакции
     */
    @JsonIgnore
    @NullForNonAdmin
    private Long userId;

    /**
     * Номер карты, используемой в качестве источника транзакции
     */
    @NotNull
    @NotBlank
    private String sourceNumber;

    /**
     * Номер карты, используемой в качестве получателя средств или цели операции
     */
    private String destinationNumber;

    /**
     * Тип транзакции
     */
    @NotNull(message = "Укажите тип транзакции")
    private String transactionType;

    /**
     * Сумма транзакции
     */
    @NotNull
    @Positive(message = "Введите положительное значение")
    private Integer amount;
}
