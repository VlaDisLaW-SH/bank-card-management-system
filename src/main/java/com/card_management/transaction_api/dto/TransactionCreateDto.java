package com.card_management.transaction_api.dto;

import com.card_management.transaction_api.enumeration.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для создания новой транзакции")
public class TransactionCreateDto {

    @Schema(
            description = "Номер карты-источника (отправителя средств)",
            example = "1234567812345678",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 16,
            maxLength = 16
    )
    @NotNull(message = "Номер карты-источника обязателен")
    @NotBlank(message = "Номер карты-источника не должен быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать ровно 16 цифр")
    private String sourceNumber;

    @Schema(
            description = "Номер карты-получателя",
            example = "8765432187654321",
            minLength = 16,
            maxLength = 16
    )
    @Size(min = 16, max = 16, message = "Номер карты должен содержать ровно 16 цифр")
    private String destinationNumber;

    @Schema(
            description = "Тип транзакции",
            example = "TRANSFER",
            implementation = TransactionType.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Укажите тип транзакции")
    @NotBlank(message = "Тип транзакции не должен быть пустым")
    private String transactionType;

    @Schema(
            description = "Сумма транзакции",
            example = "1500",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите сумму транзакции")
    @Positive(message = "Введите положительное значение")
    private Integer amount;
}
