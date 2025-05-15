package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardStatus;
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
@Schema(description = "DTO для представления информации о карте")
public class CardDto {

    @Schema(description = "ID карты", example = "1")
    private Long id;

    @Schema(description = "UUID карты", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID uuid;

    @Schema(description = "Замаскированный номер карты в формате 0000****0000", example = "1234****5678")
    private String maskNumber;

    @Schema(description = "UUID владельца карты", example = "a8a1c0d3-f623-4b02-a9cc-45a0c0bdc1ff")
    private UUID ownerUuid;

    @Schema(description = "Месяц окончания действия карты", example = "12")
    private Integer validityPeriodMonth;

    @Schema(description = "Год окончания действия карты", example = "2027")
    private Integer validityPeriodYear;

    @Schema(description = "Статус карты",
            example = "ACTIVE",
            implementation = CardStatus.class
    )
    private CardStatus status;

    @Schema(description = "Баланс карты", example = "5000", minimum = "0")
    private Integer balance;

    @Schema(description = "Дата и время создания карты",
            example = "2024-05-15T13:45:00",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления карты",
            example = "2025-01-10T09:30:00",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
