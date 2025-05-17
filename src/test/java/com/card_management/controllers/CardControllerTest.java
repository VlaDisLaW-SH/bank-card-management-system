package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.cards_api.dto.CardAdminFilterDto;
import com.card_management.cards_api.dto.CardChangeStatusDto;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.factory.integration.CardTestFactory;
import com.card_management.factory.integration.UserTestFactory;
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

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
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
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestFactory userTestFactory;

    @Autowired
    private CardTestFactory cardTestFactory;

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

        cardTestFactory.createCard(
                "4486441729154030",
                2L,
                "ACTIVE",
                5000
        );

        cardTestFactory.createCard(
                "4024007123874108",
                2L,
                "ACTIVE",
                7500
        );
    }

    @Test
    void getCards_ValidPagination_ReturnsListOfCards() throws Exception {
        mockMvc.perform(get("/cards")
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "balance"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(2));
    }

    @Test
    void getCards_InvalidSortParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/cards")
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "nonexistentField"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Недопустимое поле сортировки: nonexistentField"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void getCardById_ValidId_ReturnsCard() throws Exception {
        var cardId = 1L;
        mockMvc.perform(get("/cards/{id}", cardId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.maskNumber").value("4486****4030"));
    }

    @Test
    void getCardById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/cards/{id}", 999L)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message").value("Карта с ID 999 не найдена"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void createCard_ValidData_ReturnsCreatedCard() throws Exception {
        var cardDto = new CardCreateDto();
        cardDto.setCardNumber("4111111111111111");
        cardDto.setOwnerId(2L);
        cardDto.setValidityPeriodMonth(12);
        cardDto.setValidityPeriodYear(30);
        cardDto.setStatus("ACTIVE");
        cardDto.setBalance(1000);

        mockMvc.perform(post("/cards")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.maskNumber").value("4111****1111"));
    }

    @Test
    void createCard_InvalidCardNumberAndYear_ReturnsBadRequest() throws Exception {
        var cardDto = new CardCreateDto();
        cardDto.setCardNumber("1234");
        cardDto.setOwnerId(2L);
        cardDto.setValidityPeriodMonth(12);
        cardDto.setValidityPeriodYear(5);
        cardDto.setStatus("ACTIVE");
        cardDto.setBalance(1000);

        mockMvc.perform(post("/cards")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cardDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[?(@.field == 'cardNumber')].message")
                        .value(containsInAnyOrder(
                                "Номер карты должен содержать ровно 16 цифр",
                                "Недействительный номер карты"
                        )))
                .andExpect(jsonPath("$.errors[?(@.field == 'cardNumber')]").value(hasSize(2)))
                .andExpect(jsonPath("$.errors[?(@.field == 'validityPeriodYear')].message")
                        .value("Год должен быть не менее 10"));
    }

    @Test
    void deleteCard_ExistingId_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/cards/{id}", 1L)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteCard_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/cards/{id}", 999L)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message").value("Карта с ID 999 не найдена"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void getUserCards_ExistingUserId_ReturnsCardList() throws Exception {
        var existingUserId = 2L;
        mockMvc.perform(get("/cards/userCards/{userId}", existingUserId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].maskNumber").value("4486****4030"))
                .andExpect(jsonPath("$[1].maskNumber").value("4024****4108"));
    }

    @Test
    void getUserCards_NonExistingUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/cards/userCards/{userId}", 999L)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message").value("Пользователь с ID 999 не найден"))
                .andExpect(jsonPath("$.errors[0].field").doesNotExist());
    }

    @Test
    void getMyCards_ExistingUserId_ReturnsCardList() throws Exception {
        var existingUserId = 2L;
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        mockMvc.perform(get("/cards/my", existingUserId)
                        .header("Authorization", accessTokenUser))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].maskNumber").value("4486****4030"))
                .andExpect(jsonPath("$[1].maskNumber").value("4024****4108"));
    }

    @Test
    void setBlockedStatusForCard_ValidRequest_ReturnsOk() throws Exception {
        var validDigits = "4030";
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");
        mockMvc.perform(post("/cards/block/" + validDigits)
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void setBlockedStatusForCard_InvalidCardDigits_ReturnsBadRequest() throws Exception {
        var NonValidDigits = "12x4";
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");
        mockMvc.perform(post("/cards/block/" + NonValidDigits)
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].message").value("Введите последние четыре цифры карты."))
                .andExpect(jsonPath("$.errors[0].field").value("cardLastFourDigits"));
    }

    @Test
    void setCardStatus_ValidData_ReturnsOk() throws Exception {
        var validDto = new CardChangeStatusDto();
        validDto.setOwnerId(2L);
        validDto.setLastFourDigitsCardNumber("4030");
        validDto.setStatus("BLOCKED");

        mockMvc.perform(post("/cards/setStatus")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }

    @Test
    void setCardStatus_InvalidData_ReturnsBadRequest() throws Exception {
        var invalidDto = new CardChangeStatusDto();
        invalidDto.setOwnerId(null);
        invalidDto.setLastFourDigitsCardNumber("123");
        invalidDto.setStatus("");

        mockMvc.perform(post("/cards/setStatus")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[?(@.field == 'ownerId')].message")
                        .value("ID владельца обязателен"))
                .andExpect(jsonPath("$.errors[?(@.field == 'lastFourDigitsCardNumber')].message")
                        .value("Введите 4 последние цифры номера карты"))
                .andExpect(jsonPath("$.errors[?(@.field == 'status')].message")
                        .value("Статус не должен быть пустым"));
    }

    @Test
    void filterCards_ValidRequest_ReturnsOk() throws Exception {
        var validDto = new CardFilterDto();
        validDto.setPage(1);
        validDto.setSize(10);
        validDto.setSortBy("balance");
        validDto.setSortDirection("ASC");
        validDto.setStatus("ACTIVE");
        validDto.setGreaterThanBalance(5500);

        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");
        var uuidOwnerCards = userRepository.findByEmail("ivanov@example.com")
                .map(User::getUuid)
                .map(UUID::toString)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/cards/my/filter")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.cards[0].maskNumber").value("4024****4108"))
                .andExpect(jsonPath("$.cards[0].ownerUuid").value(uuidOwnerCards));
    }

    @Test
    void filterCards_InvalidFields_ReturnsBadRequest() throws Exception {
        var invalidDto = new CardFilterDto();
        invalidDto.setGreaterThanBalance(-100);
        invalidDto.setCreatedAfter("2025/01/01");

        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        mockMvc.perform(post("/cards/my/filter")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[?(@.field == 'greaterThanBalance')].message")
                        .value("Введите положительное значение"))
                .andExpect(jsonPath("$.errors[?(@.field == 'createdAfter')].message")
                        .value("Некорректный формат даты. Ожидается yyyy-MM-dd."));
    }

    @Test
    void filterCardsForAdmin_ValidRequest_ReturnsOk() throws Exception {
        var cardFilter = new CardFilterDto();
        cardFilter.setPage(1);
        cardFilter.setSize(10);
        cardFilter.setSortBy("balance");
        cardFilter.setSortDirection("DESC");
        cardFilter.setStatus("ACTIVE");

        var filterDto = new CardAdminFilterDto();
        filterDto.setOwnerId(2L);
        filterDto.setCardFilterDto(cardFilter);

        var uuidOwnerCards = userRepository.findById(2L)
                .map(User::getUuid)
                .map(UUID::toString)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/cards/filter")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.cards[0].maskNumber").value("4024****4108"))
                .andExpect(jsonPath("$.cards[0].ownerUuid").value(uuidOwnerCards))
                .andExpect(jsonPath("$.cards[0].balance").value(7500))
                .andExpect(jsonPath("$.cards[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.cards[1].maskNumber").value("4486****4030"))
                .andExpect(jsonPath("$.cards[1].ownerUuid").value(uuidOwnerCards))
                .andExpect(jsonPath("$.cards[1].balance").value(5000))
                .andExpect(jsonPath("$.cards[1].status").value("ACTIVE"));
    }
}
