package com.card_management.cards_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для фильтрации карт (используется администратором)")
public class CardAdminFilterDto {

    @Schema(description = "ID владельца карты", example = "1001")
    private Long ownerId;

    @Schema(description = "Объект с параметрами фильтрации карт")
    @Valid
    private CardFilterDto cardFilterDto;
}
