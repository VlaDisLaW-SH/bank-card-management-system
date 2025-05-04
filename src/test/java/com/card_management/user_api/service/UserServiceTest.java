package com.card_management.user_api.service;

import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.dto.UserDto;
import com.card_management.users_api.dto.UserEnvelopDto;
import com.card_management.users_api.exception.DuplicateEmailException;
import com.card_management.users_api.mapper.UserMapper;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.users_api.service.UserService;
import com.card_management.util.unit_test.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LimitService limitService;

    @InjectMocks
    private UserService userService;

    private User user1;

    private User user2;

    private UserDto userDto1;

    private UserDto userDto2;

    private UserCreateDto userCreateDto;


    @BeforeEach
    void setUp() {
        userCreateDto = new  UserCreateDto();
        userCreateDto.setEmail("user1@example.com");
        userCreateDto.setName("Имя1");
        userCreateDto.setSurname("Фамилия1");
        userCreateDto.setPassword("password123");
        userCreateDto.setAccessType("USER");

        user1 = UserTestFactory.createUser(1L);
        userDto1 = UserTestFactory.createUserDto(1L);

        user2 = UserTestFactory.createUser(2L);
        userDto2 = UserTestFactory.createUserDto(2L);
    }

    @Test
    void getUsers_success() {
        var page = 1;
        var size = 2;
        var sort = "email";

        var users = List.of(user1, user2);
        var userPage = new PageImpl<>(users, PageRequest.of(0, size, Sort.by(sort)), users.size());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.map(user1)).thenReturn(userDto1);
        when(userMapper.map(user2)).thenReturn(userDto2);

        UserEnvelopDto result = userService.getUsers(page, size, sort);

        assertEquals(2, result.getUsers().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("user2@example.com", result.getUsers().get(1).getEmail());
    }

    @Test
    void getUsers_shouldThrow_whenSortPropertyInvalid() {
        var page = 1;
        var size = 5;
        var invalidSort = "invalidField";

        FieldsValidationException exception = assertThrows(FieldsValidationException.class, () ->
                userService.getUsers(page, size, invalidSort)
        );

        assertEquals("Недопустимое поле сортировки: invalidField", exception.getMessage());

        verify(userRepository, never()).findAll(ArgumentMatchers.<Pageable>any());
    }

    @Test
    void findById_success() {
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userMapper.map(user1)).thenReturn(userDto1);

        UserDto result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user1@example.com", result.getEmail());
    }

    @Test
    void findById_shouldThrow_whenUserNotFound() {
        var userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.findById(userId)
        );

        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
        verify(userMapper, never()).map(any(User.class));
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByEmail(user1.getEmail())).thenReturn(false);
        when(userMapper.map(userCreateDto)).thenReturn(user1);
        when(passwordEncoder.encode(userCreateDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user1)).thenReturn(user1);
        when(userMapper.map(user1)).thenReturn(userDto1);

        UserDto result = userService.create(userCreateDto);

        assertEquals(userDto1.getEmail(), result.getEmail());
        assertEquals(userDto1.getAccessType(), result.getAccessType());
        verify(limitService).setDefaultLimits(user1);
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        var createDto = new UserCreateDto();
        createDto.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail(createDto.getEmail())).thenReturn(true);

        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                () -> userService.create(createDto));
        assertEquals("Email уже используется", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(limitService, never()).setDefaultLimits(any());
    }

    @Test
    void createUser_shouldThrow_whenAccessTypeInvalid() {
        var createDto = new UserCreateDto();
        createDto.setEmail("user@example.com");
        createDto.setAccessType("INVALID_ROLE");

        when(userRepository.existsByEmail(createDto.getEmail())).thenReturn(false);

        FieldsValidationException exception = assertThrows(FieldsValidationException.class,
                () -> userService.create(createDto));
        assertEquals("Некорректная роль для пользователя: INVALID_ROLE", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(limitService, never()).setDefaultLimits(any());
    }

    @Test
    void delete_success() {
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        userService.delete(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user1);
    }

    @Test
    void delete_shouldThrow_whenUserNotFound() {
        var userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.delete(userId)
        );

        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }
}
