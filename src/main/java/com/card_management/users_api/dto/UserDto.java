package com.card_management.users_api.dto;

import com.card_management.users_api.enumeration.UserAccessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для представления данных пользователя")
public class UserDto {
    @Schema(description = "Уникальный идентификатор пользователя в БД", example = "1")
    private Long id;

    @Schema(description = "UUID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String surname;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;

    @Schema(description = "Отчество пользователя", example = "Иванович")
    private String middleName;

    @Schema(
            description = "Адрес электронной почты",
            example = "user@example.com",
            format = "email"
    )
    private String email;

    @Schema(
            description = "Роль пользователя в системе",
            example = "ADMIN",
            implementation = UserAccessType.class
    )
    private UserAccessType accessType;

    @Schema(
            description = "Дата и время создания пользователя",
            example = "2023-05-15T14:30:00",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
