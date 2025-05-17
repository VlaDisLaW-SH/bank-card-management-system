package com.card_management.controllers;

import com.card_management.technical.exception.record.ErrorResponse;
import com.card_management.technical.util.JwtUtil;
import com.card_management.users_api.dto.AuthRequest;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация и регистрация", description = "API для регистрации и входа пользователей")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Регистрирует нового пользователя на основе переданных данных"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(
                            type = "string",
                            example = "Пользователь с Email user@example.com зарегистрирован."))
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или дубликат email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @Valid @RequestBody UserCreateDto userDto
    ) {
        var user = userService.create(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Пользователь с Email " + user.getEmail() + " зарегистрирован.");
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Принимает email и пароль, возвращает JWT токен при успешной аутентификации"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аутентификация успешна, токен выдан",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Неверные учетные данные",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Parameter(description = "Учетные данные пользователя", required = true)
            @Valid @RequestBody AuthRequest request
    ) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(token);
    }
}
