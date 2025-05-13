package com.card_management.users_api.dto;

import com.card_management.users_api.enumeration.UserAccessType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для создания нового пользователя")
public class UserCreateDto {

    @Schema(
            description = "Фамилия пользователя",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Иванов"
    )
    @NotBlank(message = "Фамилия не может быть пустой")
    private String surname;

    @Schema(
            description = "Имя пользователя",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Иван"
    )
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Schema(
            description = "Отчество пользователя",
            example = "Иванович",
            nullable = true
    )
    private String middleName;

    @Schema(
            description = "Адрес электронной почты",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "user@example.com",
            format = "email"
    )
    @NotNull(message = "Email не может быть null")
    @Email(message = "Некорректный формат email")
    @Column(unique = true)
    private String email;

    @Schema(
            description = "Пароль пользователя (должен содержать от 8 до 72 символов)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "securePassword123!",
            minLength = 8,
            maxLength = 72
    )
    @NotNull(message = "Пароль не может быть null")
    @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
    private String password;

    @Schema(
            description = "Роль пользователя в системе",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "USER",
            implementation = UserAccessType.class
    )
    @NotNull(message = "Необходимо назначить роль пользователя в системе")
    private String accessType;
}
