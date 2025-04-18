package com.card_management.users_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AuthRequest {
    /**
     * Адрес электронной почты
     */
    private String email;

    /**
     * Пароль
     */
    private String password;
}
