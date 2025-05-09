package com.card_management.cards_api.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CardAdminFilterDto {
    /**
     * ID владельца карты
     */
    private Long ownerId;

    /**
     * Dto с данными для фильтрации карт
     */
    @Valid
    private CardFilterDto cardFilterDto;
}
