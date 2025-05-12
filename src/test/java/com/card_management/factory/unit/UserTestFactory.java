package com.card_management.factory.unit;

import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.enumeration.UserAccessType;
import com.card_management.users_api.model.User;

public class UserTestFactory {

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        user.setName("Имя" + id);
        user.setSurname("Фамилия" + id);
        user.setAccessType(UserAccessType.USER);
        return user;
    }

    public static UserDto createUserDto(Long id) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail("user" + id + "@example.com");
        dto.setName("Имя" + id);
        dto.setSurname("Фамилия" + id);
        dto.setAccessType(UserAccessType.USER);
        return dto;
    }
}
