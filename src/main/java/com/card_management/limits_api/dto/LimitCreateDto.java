package com.card_management.limits_api.dto;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.transaction_api.enumeration.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Schema(description = "DTO для создания нового лимита")
public class LimitCreateDto {

    @Schema(
            description = "ID пользователя, для которого устанавливается лимит",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите ID пользователя")
    private Long userId;

    @Schema(
            description = "Тип лимита",
            example = "DAILY",
            implementation = LimitType.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Необходимо указать тип лимита")
    @NotBlank(message = "Необходимо указать тип лимита")
    private String limitType;

    @Schema(
            description = "Тип транзакции",
            example = "TRANSFER",
            implementation = TransactionType.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Необходимо указать тип транзакции")
    @NotBlank(message = "Необходимо указать тип транзакции")
    private String transactionType;

    @Schema(
            description = "Сумма лимита (должна быть положительной)",
            example = "10000",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите сумму лимита")
    @Positive(message = "Введите положительное значение")
    private Integer limitAmount;
}
