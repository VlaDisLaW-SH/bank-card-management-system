package com.card_management.users_api.service;

import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.dto.UserEnvelopDto;
import com.card_management.users_api.exception.DuplicateEmailException;
import com.card_management.users_api.mapper.UserMapper;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserEnvelopDto getUsers(int page, int size, String sort) {
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

    public UserDto create(UserCreateDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Email уже используется");
        }
        var user = userMapper.map(userDto);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        userRepository.delete(user);
    }
}
