package com.card_management.limits_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LimitCreateDto {
    /**
     * ID пользователя для которого устанавливается лимит
     */
    @NotNull(message = "Введите ID пользователя")
    private Long userId;

    /**
     * Тип лимита
     */
    @NotNull(message = "Необходимо указать тип лимита")
    @NotBlank(message = "Необходимо указать тип лимита")
    private String limitType;

    /**
     * Тип транзакции
     */
    @NotNull(message = "Необходимо указать тип транзакции")
    @NotBlank(message = "Необходимо указать тип транзакции")
    private String transactionType;

    /**
     * Сумма лимита
     */
    @NotNull(message = "Введите сумму лимита")
    @Positive(message = "Введите положительное значение")
    private Integer limitAmount;
}
