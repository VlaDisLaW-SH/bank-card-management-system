package com.card_management.limits_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип лимита
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum LimitType {
    DAILY("Суточный лимит"),
    MONTHLY("Месячный лимит");

    private final String description;
}
