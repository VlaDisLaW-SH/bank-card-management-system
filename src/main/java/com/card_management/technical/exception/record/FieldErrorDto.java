package com.card_management.technical.exception.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldErrorDto(
        @Schema(description = "Наименование поля", example = "Наименование поля")
        String field,
        @Schema(description = "Сообщение об ошибке", example = "Текст ошибки")
        String message) {}
