package com.card_management.cards_api.enumeration;

import com.card_management.technical.enumeration.FieldEnumerable;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для сортировки карт
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Допустимые значения для сортировки карт")
public enum CardSortFields implements FieldEnumerable {
    ID("id"),
    UUID("uuid"),
    MASK_NUMBER("maskNumber"),
    OWNER_UUID("ownerUuid"),
    VALIDITY_PERIOD_MONTH("validityPeriodMonth"),
    VALIDITY_PERIOD_YEAR("validityPeriodYear"),
    STATUS("status"),
    BALANCE("balance"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    @Override
    @JsonValue
    public String toString() {
        return field;
    }
}
