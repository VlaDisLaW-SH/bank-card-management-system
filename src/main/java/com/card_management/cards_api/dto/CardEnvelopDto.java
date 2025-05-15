package com.card_management.cards_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Обертка для постраничного списка карт")
public class CardEnvelopDto {

    @Schema(description = "Список карт")
    private List<CardDto> cards;

    @Schema(description = "Общее количество элементов", example = "10")
    private long totalElements;

    @Schema(description = "Общее количество страниц", example = "5")
    private int totalPages;
}
