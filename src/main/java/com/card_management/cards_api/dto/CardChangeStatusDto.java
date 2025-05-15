package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для изменения статуса карты")
public class CardChangeStatusDto {

    @Schema(
            description = "ID владельца карты",
            example = "1001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID владельца обязателен")
    private Long ownerId;

    @Schema(
            description = "Последние 4 цифры номера карты",
            example = "1234",
            minLength = 4,
            maxLength = 4,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите 4 последние цифры номера карты")
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String lastFourDigitsCardNumber;

    @Schema(
            description = "Новый статус карты",
            example = "BLOCKED",
            implementation = CardStatus.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Статус обязателен")
    @NotEmpty(message = "Статус не должен быть пустым")
    private String status;
}
