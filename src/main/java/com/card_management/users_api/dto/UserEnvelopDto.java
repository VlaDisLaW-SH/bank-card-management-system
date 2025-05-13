package com.card_management.users_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Обертка для постраничного списка пользователей")
public class UserEnvelopDto {

    @Schema(
            description = "Список пользователей",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<UserDto> users;

    @Schema(
            description = "Общее количество элементов",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private long totalElements;

    @Schema(
            description = "Общее количество страниц",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int totalPages;
}
