package com.card_management.technical.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для направления сортировки
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Допустимые значения для направления сортировки")
public enum SortDirection {
    ASC("По возрастанию"),
    DESC("По убыванию");

    private final String field;
}
