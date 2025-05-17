package com.card_management.transaction_api.dto;

import com.card_management.transaction_api.enumeration.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO для представления информации о транзакции")
public class TransactionDto {

    @Schema(
            description = "UUID транзакции",
            example = "e7e31bc7-4d22-4d36-a907-b3ad2f46f3bb"
    )
    private UUID uuid;

    @Schema(
            description = "UUID пользователя, инициировавшего транзакцию",
            example = "9b248edf-2127-4a4b-a7d9-875bca7cd07a"
    )
    private UUID userUuid;

    @Schema(
            description = "Замаскированный номер карты-источника в формате 0000****0000",
            example = "1234****5678"
    )
    private String maskedSource;

    @Schema(
            description = "Замаскированный номер карты-получателя в формате 0000****0000",
            example = "8765****4321"
    )
    private String maskedDestination;

    @Schema(
            description = "Тип транзакции",
            example = "TRANSFER",
            implementation = TransactionType.class
    )
    private TransactionType transactionType;

    @Schema(
            description = "Сумма транзакции",
            example = "2500",
            minimum = "0"
    )
    private Integer amount;

    @Schema(
            description = "Дата и время создания транзакции",
            example = "2024-06-01T15:30:00",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
