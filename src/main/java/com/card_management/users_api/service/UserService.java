package com.card_management.users_api.service;

import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.enumeration.FieldEnumerable;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.dto.UserEnvelopDto;
import com.card_management.users_api.enumeration.UserAccessType;
import com.card_management.users_api.enumeration.UserSortFields;
import com.card_management.users_api.exception.DuplicateEmailException;
import com.card_management.users_api.mapper.UserMapper;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final LimitService limitService;

    private final PasswordEncoder passwordEncoder;

    public UserEnvelopDto getUsers(int page, int size, String sort) {
        if (!FieldEnumerable.containsField(UserSortFields.class, sort)) {
            throw new FieldsValidationException("Недопустимое поле сортировки: " + sort);
        }
        var pageRequest = PageRequest.of(page - 1, size, Sort.by(sort));
        var userPage = userRepository.findAll(pageRequest);
        var userDto = userPage.stream()
                .map(userMapper::map)
                .toList();
        return new UserEnvelopDto(
                userDto,
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }

    public UserDto findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        return userMapper.map(user);
    }

    @Transactional
    public UserDto create(UserCreateDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Email уже используется");
        }
        if (!EnumUtils.isValidEnumIgnoreCase(UserAccessType.class, userDto.getAccessType())) {
            throw new FieldsValidationException("Некорректная роль для пользователя: " + userDto.getAccessType());
        }
        var user = userMapper.map(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        limitService.setDefaultLimits(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        userRepository.delete(user);
    }
}
