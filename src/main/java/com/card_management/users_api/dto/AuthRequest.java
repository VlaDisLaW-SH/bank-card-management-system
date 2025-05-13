package com.card_management.users_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для аутентификации пользователя")
public class AuthRequest {

    @Schema(
            description = "Адрес электронной почты",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "user@example.com",
            format = "email"
    )
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(
            description = "Пароль",
            requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY,
            example = "mySecurePassword123!",
            minLength = 8,
            maxLength = 72
    )
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
    private String password;
}
