package com.card_management.cards_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CardEnvelopDto {
    /**
     * Список карт
     */
    private List<CardDto> cards;

    /**
     * Кол-во элементов
     */
    private long totalElements;

    /**
     * Кол-во страниц
     */
    private int totalPages;
}
