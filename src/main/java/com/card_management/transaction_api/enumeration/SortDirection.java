package com.card_management.transaction_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для направления сортировки
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum SortDirection {
    ASC("По возрастанию"),
    DESC("По убыванию");

    private final String field;
}
