package com.card_management.users_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Допустимые значения для сортировки пользователей
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum UserSortFields {
    ID("id"),
    UUID("uuid"),
    SURNAME("surname"),
    NAME("name"),
    MIDDLE_NAME("middleName"),
    EMAIL("email"),
    ACCESS_TYPE("accessType"),
    CREATED_AT("createdAt");

    private final String field;
}
