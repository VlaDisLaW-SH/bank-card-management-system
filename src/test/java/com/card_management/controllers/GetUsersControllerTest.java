package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.util.AdminTestHelper;
import com.card_management.util.UserTestHelper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import({SecurityConfig.class, AuthController.class})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GetUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void getUsers_ValidPagination_ReturnsListOfUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(3L))
                .andExpect(jsonPath("$.users[1].email").value("ivanov@example.com"))
                .andExpect(jsonPath("$.users[2].email").value("petrov@example.com"));
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
                .andExpect(content().string("Page index must not be less than zero"));
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
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.surname").value("Иванов"))
                .andExpect(jsonPath("$.accessType").value("USER"));
    }

    @Test
    void getUser_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 7L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(content().string("Пользователь с ID 7 не найден"));
    }
}
