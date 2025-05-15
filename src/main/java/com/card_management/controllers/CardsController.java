package com.card_management.controllers;

import com.card_management.cards_api.dto.*;
import com.card_management.cards_api.service.CardService;
import com.card_management.controllers.common.CardValidator;
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
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Управление картами", description = "API для управления банковскими картами")
public class CardsController {

    private final CardService cardService;
    private final CardValidator cardValidator;

    @Operation(
            summary = "Получить список карт",
            description = "Возвращает пагинированный список всех карт. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт",
                    content = @Content(schema = @Schema(implementation = CardEnvelopDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardEnvelopDto> getCards(
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        CardEnvelopDto cardEnvelopDto = cardService.getCards(page, size, sort);
        return ResponseEntity.ok(cardEnvelopDto);
    }

    @Operation(
            summary = "Получить карту по ID",
            description = "Возвращает карту по её ID. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта найдена",
                    content = @Content(schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> findById(
            @Parameter(description = "ID карты", example = "1") @PathVariable Long id) {
        var card = cardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @Operation(
            summary = "Создать новую карту",
            description = "Создает карту. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                    content = @Content(schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardCreateDto cardData) {
        cardValidator.validateCreateCard(cardData);
        var card = cardService.create(cardData);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @Operation(
            summary = "Удалить карту",
            description = "Удаляет карту по ID. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@Parameter(description = "ID карты", example = "1") @PathVariable Long id) {
        cardService.delete(id);
    }

    @Operation(
            summary = "Получить карты пользователя (для ADMIN)",
            description = "Возвращает список карт по ID пользователя. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карты успешно получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardDto.class)))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(path = "/userCards/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDto>> getUserCards(
            @Parameter(description = "ID пользователя", example = "1") @PathVariable Long userId) {
        List<CardDto> cardDtoList = cardService.getUserCards(userId);
        return ResponseEntity.ok(cardDtoList);
    }

    @Operation(
            summary = "Получить свои карты",
            description = "Возвращает карты текущего пользователя. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карты успешно получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardDto.class)))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDto>> getMyCards(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CardDto> cardDtoList = cardService.getUserCards(userDetails.getId());
        return ResponseEntity.ok(cardDtoList);
    }

    @Operation(
            summary = "Заблокировать карту по последним 4 цифрам",
            description = "Устанавливает статус блокировки карты текущего пользователя. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/block/{cardLastFourDigits}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public void setBlockedStatusForCard(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Последние 4 цифры карты", example = "1234")
            @PathVariable
            @Pattern(regexp = "\\d{4}", message = "Введите последние четыре цифры карты.")
            String cardLastFourDigits
    ) {
        cardService.setBlockedStatusForCard(userDetails.getId(), cardLastFourDigits);
    }

    @Operation(
            summary = "Установить статус карты",
            description = "Меняет статус карты. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно установлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(path = "/setStatus")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void setCardStatus(@Valid @RequestBody CardChangeStatusDto dataDto) {
        cardValidator.validateSetCardStatus(dataDto);
        cardService.setCardStatus(
                dataDto.getOwnerId(),
                dataDto.getLastFourDigitsCardNumber(),
                dataDto.getStatus()
        );
    }

    @Operation(
            summary = "Фильтрация карт (ADMIN)",
            description = "Фильтрует список карт по заданным параметрам. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фильтрация выполнена",
                    content = @Content(schema = @Schema(implementation = CardEnvelopDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(path = "/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardEnvelopDto> filterCards(@Valid @RequestBody CardAdminFilterDto filterDto) {
        cardValidator.validateFilterCard(filterDto.getCardFilterDto());
        var cards = cardService.filterCardsForAdmin(filterDto);
        return ResponseEntity.ok(cards);
    }

    @Operation(
            summary = "Фильтрация моих карт",
            description = "Фильтрует карты текущего пользователя по заданным параметрам. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фильтрация выполнена",
                    content = @Content(schema = @Schema(implementation = CardEnvelopDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(path = "/my/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardEnvelopDto> filterCards(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CardFilterDto filterDto
    ) {
        cardValidator.validateFilterCard(filterDto);
        var cards = cardService.filterCards(filterDto, userDetails.getId());
        return ResponseEntity.ok(cards);
    }
}
