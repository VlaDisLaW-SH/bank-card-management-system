package com.card_management.factory.integration;

import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.service.UserService;
import com.card_management.util.AccessTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserTestFactory {

    @Autowired
    private UserService userService;

    @Autowired
    private AccessTokenHelper accessTokenHelper;

    private final Map<String, String> tokenCache = new HashMap<>();

    public String createUser(String email, String password, String accessType) throws Exception {
        var dto = new UserCreateDto();
        dto.setSurname("Тест");
        dto.setName("Пользователь");
        dto.setMiddleName("Тестович");
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setAccessType(accessType);
        userService.create(dto);
        var token = accessTokenHelper.obtainAccessToken(email, password);
        tokenCache.put(email, token);
        return token;
    }

    public String getToken(String email) {
        return tokenCache.get(email);
    }
}
