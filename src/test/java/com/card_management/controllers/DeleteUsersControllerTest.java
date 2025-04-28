package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.users_api.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import({SecurityConfig.class, AuthController.class})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeleteUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminTestHelper adminTestHelper;

    @Autowired
    private UserTestHelper userTestHelper;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        accessToken = adminTestHelper.createAdmin();
        userTestHelper.createUsers();
    }

    @Test
    void deleteUser_ExistingId_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/users/{id}", 3L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        boolean exists = userRepository.existsById(3L);
        assertFalse(exists, "Пользователь должен быть удален");
    }

    @Test
    void deleteUser_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/users/{id}", 777L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(content().string("Пользователь с ID 777 не найден"));
    }
}
