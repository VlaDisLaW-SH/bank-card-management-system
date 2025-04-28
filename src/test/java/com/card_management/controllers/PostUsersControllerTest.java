package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.util.AdminTestHelper;
import com.card_management.util.UserTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import({SecurityConfig.class, AuthController.class})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Transactional
class PostUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminTestHelper adminTestHelper;

    @Autowired
    private UserTestHelper userTestHelper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        accessToken = adminTestHelper.createAdmin();
        userTestHelper.createUsers();
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
                .andExpect(content().string("Email уже используется"));
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
                .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
    }
}
