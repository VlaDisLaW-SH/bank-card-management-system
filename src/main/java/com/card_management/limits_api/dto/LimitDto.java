package com.card_management.limits_api.dto;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для представления информации о лимите")
public class LimitDto {

    @Schema(description = "ID лимита", example = "1")
    private Long id;

    @Schema(description = "UUID лимита", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "UUID пользователя, для которого установлен лимит",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userUuid;

    @Schema(description = "Тип лимита",
            example = "DAILY",
            implementation = LimitType.class)
    private LimitType limitType;

    @Schema(description = "Тип транзакции",
            example = "TRANSFER",
            implementation = TransactionType.class)
    private TransactionType transactionType;

    @Schema(description = "Сумма лимита",
            example = "10000",
            minimum = "0")
    private Integer limitAmount;

    @Schema(description = "Сумма остатков по лимиту",
            example = "5000",
            minimum = "0")
    private Integer balances;

    @Schema(description = "Дата и время создания лимита",
            example = "2023-05-15T14:30:00",
            type = "string",
            format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время обновления лимита",
            example = "2023-05-15T15:45:00",
            type = "string",
            format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
