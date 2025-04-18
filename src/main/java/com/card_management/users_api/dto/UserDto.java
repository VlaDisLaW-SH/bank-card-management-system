package com.card_management.users_api.dto;

import com.card_management.users_api.enumeration.UserAccessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;

    /**
     * UUID пользователя
     */
    private UUID uuid;

    /**
     * Фамилия
     */
    private String surname;

    /**
     * Имя
     */
    private String name;

    /**
     * Отчество
     */
    private String middleName;

    /**
     * Адрес электронной почты
     */
    private String email;

    /**
     * Роль пользователя в системе
     */
    private UserAccessType accessType;

    /**
     * Дата и время создания пользователя
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
