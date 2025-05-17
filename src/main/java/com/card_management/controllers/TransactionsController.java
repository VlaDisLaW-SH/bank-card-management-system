package com.card_management.controllers;

import com.card_management.controllers.common.TransactionValidator;
import com.card_management.technical.exception.record.ErrorResponse;
import com.card_management.transaction_api.dto.*;
import com.card_management.transaction_api.service.TransactionService;
import com.card_management.users_api.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@Validated
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Управление транзакциями", description = "API для работы с транзакциями пользователей")
public class TransactionsController {

    private final TransactionService transactionService;
    private final TransactionValidator transactionValidator;

    @Operation(
            summary = "Получить список всех транзакций",
            description = "Возвращает пагинированный список всех транзакций. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций получен",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> getTransactions(
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getTransactions(page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @Operation(
            summary = "Получить список транзакций текущего пользователя",
            description = "Возвращает транзакции авторизованного пользователя. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций пользователя получен",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> getTransactions(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto =
                transactionService.getUserTransactions(userDetails.getId(), page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @Operation(
            summary = "Получить транзакцию по ID",
            description = "Возвращает транзакцию по её ID. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакция найдена",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDto> findById(
            @Parameter(description = "ID транзакции", example = "1") @PathVariable Long id) {
        var transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Создать транзакцию",
            description = "Создает новую транзакцию. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Транзакция успешно создана",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> create(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransactionCreateDto transactionData
    ) {
        transactionValidator.validateCreateTransaction(transactionData);
        var transaction = transactionService.create(transactionData, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @Operation(
            summary = "Удалить транзакцию",
            description = "Удаляет транзакцию по ID. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакция удалена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(
            @Parameter(description = "ID транзакции", example = "1") @PathVariable Long id) {
        transactionService.delete(id);
    }

    @Operation(
            summary = "Получить транзакции пользователя по ID",
            description = "Возвращает транзакции по ID пользователя. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций получен",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/transactionsByUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> getUserTransactions(
            @Parameter(description = "ID пользователя", example = "123") @PathVariable Long userId,
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto =
                transactionService.getUserTransactions(userId, page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @Operation(
            summary = "Получить транзакции по последним 4 цифрам карты (для ADMIN)",
            description = "Возвращает транзакции по последним 4 цифрам карты и ID пользователя. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакции успешно получены",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping(path = "/transactionsByCard")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> getCardTransactions(
            @Parameter(description = "Данные для фильтрации по карте", required = true)
            @Valid @RequestBody TransactionByCardDto transactionByCardDto,
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto = transactionService.getCardTransactionsForAdmin(
                transactionByCardDto,
                page,
                size,
                sort
        );
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @Operation(
            summary = "Получить транзакции по последним цифрам карты (для текущего пользователя)",
            description = "Возвращает транзакции по последним 4 цифрам карты. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакции получены",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/my/byCard/{cardLastFourDigits}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> getUserTransactionsByCard(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Последние 4 цифры карты", example = "1234")
            @PathVariable
            @Pattern(regexp = "\\d{4}", message = "Введите последние четыре цифры карты.")
            String cardLastFourDigits,
            @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортировки", example = "id") @RequestParam(defaultValue = "id") String sort
    ) {
        TransactionEnvelopDto transactionEnvelopDto =
                transactionService.getUserTransactionsByCard(userDetails.getId(), cardLastFourDigits, page, size, sort);
        return ResponseEntity.ok(transactionEnvelopDto);
    }

    @Operation(
            summary = "Фильтрация транзакций (для ADMIN)",
            description = "Фильтрует список транзакций по заданным параметрам. Только для ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фильтрация выполнена",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionEnvelopDto> filterTransactions(
            @Valid @RequestBody TransactionAdminFilterDto filterDto
    ) {
        transactionValidator.validateFilterTransaction(filterDto.getTransactionFilterDto());
        var transactions = transactionService.filterTransactionsForAdmin(filterDto);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Фильтрация транзакций текущего пользователя",
            description = "Фильтрует список транзакций текущего пользователя. Только для USER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фильтрация выполнена",
                    content = @Content(schema = @Schema(implementation = TransactionEnvelopDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/my/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionEnvelopDto> filterUserTransactions(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransactionFilterDto filterDto
    ) {
        transactionValidator.validateFilterTransaction(filterDto);
        var transactions = transactionService.filterTransactions(filterDto, userDetails.getId());
        return ResponseEntity.ok(transactions);
    }
}
