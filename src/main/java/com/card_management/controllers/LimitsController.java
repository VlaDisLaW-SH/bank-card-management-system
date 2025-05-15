package com.card_management.controllers;

import com.card_management.controllers.common.LimitValidator;
import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.dto.LimitEnvelopDto;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.record.ErrorResponse;
import com.card_management.users_api.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/limits")
@RequiredArgsConstructor
@Tag(name = "Управление лимитами", description = "API для управления лимитами пользователей")
public class LimitsController {

    private final LimitService limitService;
    private final LimitValidator limitValidator;

    @Operation(
            summary = "Получить список лимитов",
            description = "Возвращает пагинированный список всех лимитов. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка лимитов",
                    content = @Content(schema = @Schema(implementation = LimitEnvelopDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LimitEnvelopDto> getLimits(
            @Parameter(description = "Номер страницы", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поле для сортировки", example = "id")
            @RequestParam(defaultValue = "id") String sort
    ) {
        LimitEnvelopDto limitEnvelopDto = limitService.getLimits(page, size, sort);
        return ResponseEntity.ok(limitEnvelopDto);
    }

    @Operation(
            summary = "Получить лимит по ID",
            description = "Возвращает лимит по его идентификатору. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Лимит найден",
                    content = @Content(schema = @Schema(implementation = LimitDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Лимит не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LimitDto> findById(
            @Parameter(description = "ID лимита", required = true, example = "1")
            @PathVariable Long id) {
        var limit = limitService.findById(id);
        return ResponseEntity.ok(limit);
    }

    @Operation(
            summary = "Создать новый лимит",
            description = "Создает новый лимит. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Лимит успешно создан",
                    content = @Content(schema = @Schema(implementation = LimitDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные лимита",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LimitDto> create(@Valid @RequestBody LimitCreateDto limitData) {
        limitValidator.validateCreateLimit(limitData);
        var limit = limitService.create(limitData);
        return ResponseEntity.status(HttpStatus.CREATED).body(limit);
    }

    @Operation(
            summary = "Установить лимит",
            description = "Устанавливает лимит для пользователя. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Лимит успешно установлен"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные лимита",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping(path = "/setLimit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> setLimit(@Valid @RequestBody LimitCreateDto limitData) {
        limitValidator.validateCreateLimit(limitData);
        var message = limitService.setLimit(limitData);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Operation(
            summary = "Удалить лимит",
            description = "Удаляет лимит по его ID. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Лимит успешно удален"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Лимит не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(
            @Parameter(description = "ID лимита для удаления", required = true, example = "1")
            @PathVariable Long id) {
        limitService.delete(id);
    }

    @Operation(
            summary = "Получить лимиты пользователя (для ADMIN)",
            description = "Возвращает список лимитов для указанного пользователя. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение лимитов",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = LimitDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping(path = "/userLimit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LimitDto>> getUserLimit(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long userId) {
        var limitsList = limitService.getUserLimits(userId);
        return ResponseEntity.ok(limitsList);
    }

    @Operation(
            summary = "Получить мои лимиты",
            description = "Возвращает список лимитов для текущего пользователя. Доступно для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение лимитов",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = LimitDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping(path = "/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LimitDto>> getLimitsByUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var limitsList = limitService.getUserLimits(userDetails.getId());
        return ResponseEntity.ok(limitsList);
    }
}
