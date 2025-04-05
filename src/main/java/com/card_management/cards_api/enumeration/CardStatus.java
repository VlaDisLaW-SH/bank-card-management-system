package com.card_management.cards_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип статуса карты
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum CardStatus {
    ACTIVE("Карта активна"),
    BLOCKED("Карта заблокирована"),
    EXPIRED("У карты истек срок действия");

    private final String description;
}
