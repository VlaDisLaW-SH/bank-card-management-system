package com.card_management.limits_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Обертка для постраничного списка лимитов")
public class LimitEnvelopDto {

    @Schema(description = "Список лимитов")
    private List<LimitDto> limits;

    @Schema(description = "Общее количество элементов", example = "10")
    private long totalElements;

    @Schema(description = "Общее количество страниц", example = "5")
    private int totalPages;
}
