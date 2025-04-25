package com.card_management.users_api.mapper;

import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {}
)
public abstract class UserMapper {

    @Autowired
    private UserRepository userRepository;

    @Mapping(target = "password", ignore = true)
    public abstract User map(UserCreateDto dto);

    public abstract UserDto map(User model);

    public User mapIdToUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
    }
}
