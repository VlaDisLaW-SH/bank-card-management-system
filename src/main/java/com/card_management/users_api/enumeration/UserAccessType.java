package com.card_management.users_api.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Роль пользователя в системе
 */
@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum UserAccessType {
    ADMINISTRATOR("Администратор"),
    USER("Пользователь");

    private final String description;
}
