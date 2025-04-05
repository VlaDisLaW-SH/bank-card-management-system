package com.card_management.users_api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserCreateDto {
    /**
     * Фамилия
     */
    @NotBlank
    private String surname;

    /**
     * Имя
     */
    @NotBlank
    private String name;

    /**
     * Отчество
     */
    private String patronymic;

    /**
     * Адрес электронной почты
     */
    @NotNull
    @Column(unique = true)
    @Email
    private String email;

    /**
     * Пароль
     */
    @NotNull
    @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
    private String password;

    /**
     * Роль пользователя в системе
     */
    @NotNull(message = "Необходимо назначить роль пользователя в системе")
    private String accessType;
}
