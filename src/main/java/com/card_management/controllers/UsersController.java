package com.card_management.controllers;

import com.card_management.technical.exception.record.ErrorResponse;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.dto.UserEnvelopDto;
import com.card_management.users_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Управление пользователями", description = "Операции для работы с пользователями")
public class UsersController {

    private final UserService userService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получить список пользователей",
            description = "Возвращает постраничный список пользователей с возможностью сортировки",
            parameters = {
                    @Parameter(name = "page", description = "Номер страницы (начиная с 1)", example = "1"),
                    @Parameter(name = "size", description = "Количество элементов на странице", example = "10"),
                    @Parameter(name = "sort", description = "Поле для сортировки", example = "id")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен",
            content = @Content(schema = @Schema(implementation = UserEnvelopDto.class))
    )
    public ResponseEntity<UserEnvelopDto> index(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        UserEnvelopDto userEnvelopDto = userService.getUsers(page, size, sort);
        return ResponseEntity.ok(userEnvelopDto);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по его уникальному идентификатору"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно найден",
            content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<UserDto> show(
            @Parameter(
                    name = "id",
                    description = "Уникальный идентификатор пользователя (положительное число)",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id
    ) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с предоставленными данными"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно создан",
            content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные входные данные",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto userData) {
        var user = userService.create(userData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его уникальному идентификатору"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно удален"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public void remove(
            @Parameter(
                    name = "id",
                    description = "Уникальный идентификатор пользователя (положительное число)",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id
    ) {
        userService.delete(id);
    }
}
