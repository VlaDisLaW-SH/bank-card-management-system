package com.card_management.limits_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LimitEnvelopDto {
    /**
     * Список лимитов
     */
    private List<LimitDto> limits;

    /**
     * Кол-во элементов
     */
    private long totalElements;

    /**
     * Кол-во страниц
     */
    private int totalPages;
}
