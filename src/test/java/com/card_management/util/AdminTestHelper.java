package com.card_management.util;

import com.card_management.users_api.dto.AuthRequest;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.users_api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class AdminTestHelper {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    public String createAdmin() throws Exception {
        userRepository.deleteAll();

        var adminEmail = "admin@example.com";
        var adminPassword = "adminPassword123";

        UserCreateDto adminDto = new UserCreateDto();
        adminDto.setSurname("Администратор");
        adminDto.setName("Админи");
        adminDto.setMiddleName("Админович");
        adminDto.setEmail(adminEmail);
        adminDto.setPassword(adminPassword);
        adminDto.setAccessType("ADMIN");
        userService.create(adminDto);

        return obtainAccessToken(adminEmail, adminPassword);
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return "Bearer " + responseBody.replace("\"", "");
    }
}
