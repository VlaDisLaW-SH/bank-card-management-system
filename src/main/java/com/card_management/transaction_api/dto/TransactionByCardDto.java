package com.card_management.transaction_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для получения транзакций по последним 4 цифрам карты")
public class TransactionByCardDto {

    @Schema(
            description = "ID владельца карты",
            example = "1001",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "Введите положительное значение")
    private Long userId;

    @Schema(
            description = "Последние 4 цифры номера карты",
            example = "1234",
            minLength = 4,
            maxLength = 4,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите последние 4 цифры карты")
    @NotBlank(message = "Последние 4 цифры карты не должны быть пустыми")
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String cardLastFourDigits;
}
