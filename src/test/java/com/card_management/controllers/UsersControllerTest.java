package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.factory.integration.UserTestFactory;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import({SecurityConfig.class, AuthController.class})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTestFactory userTestFactory;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        accessToken = userTestFactory.createUser(
                "admin@example.com",
                "adminPassword123",
                "ADMIN");

        userTestFactory.createUser(
                "ivanov@example.com",
                "securePassword123",
                "USER");
    }

    @Test
    void getUsers_ValidPagination_ReturnsListOfUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.users[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$.users[1].email").value("ivanov@example.com"));
    }

    @Test
    void getUsers_InvalidPage_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", accessToken)
                        .param("page", "-1")
                        .param("size", "10")
                        .param("sort", "id"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Page index must not be less than zero"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void getUser_ExistingId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/users/{id}", 2L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("ivanov@example.com"))
                .andExpect(jsonPath("$.accessType").value("USER"));
    }

    @Test
    void getUser_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 7L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Пользователь с ID 7 не найден"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void createUser_ValidData_ReturnsCreatedUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setSurname("Иванов");
        userCreateDto.setName("Иван");
        userCreateDto.setMiddleName("Иванович");
        userCreateDto.setEmail("ivanov_ivan@example.com");
        userCreateDto.setPassword("securePassword123");
        userCreateDto.setAccessType("USER");

        mockMvc.perform(post("/users")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.email").value("ivanov_ivan@example.com"))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.surname").value("Иванов"));
    }

    @Test
    void createUser_DuplicateEmail_ReturnsConflict() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setSurname("Иванов");
        userCreateDto.setName("Иван");
        userCreateDto.setMiddleName("Иванович");
        userCreateDto.setEmail("ivanov@example.com");
        userCreateDto.setPassword("securePassword123");
        userCreateDto.setAccessType("USER");

        mockMvc.perform(post("/users")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Email уже используется"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setSurname("Иванов");
        userCreateDto.setName("Иван");
        userCreateDto.setMiddleName("Иванович");
        userCreateDto.setEmail("invalid-email");
        userCreateDto.setPassword("securePassword123");
        userCreateDto.setAccessType("USER");

        mockMvc.perform(post("/users")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].message")
                        .value("must be a well-formed email address"));
    }

    @Test
    void deleteUser_ExistingId_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/users/{id}", 2L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        boolean exists = userRepository.existsById(2L);
        assertFalse(exists, "Пользователь должен быть удален");
    }

    @Test
    void deleteUser_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/users/{id}", 999L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Пользователь с ID 999 не найден"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }
}
