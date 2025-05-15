package com.card_management.controllers;

import com.card_management.application.Application;
import com.card_management.application.configuration.SecurityConfig;
import com.card_management.factory.integration.CardTestFactory;
import com.card_management.factory.integration.TransactionTestFactory;
import com.card_management.factory.integration.UserTestFactory;
import com.card_management.transaction_api.dto.TransactionAdminFilterDto;
import com.card_management.transaction_api.dto.TransactionByCardDto;
import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionFilterDto;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
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
public class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestFactory userTestFactory;

    @Autowired
    private CardTestFactory cardTestFactory;

    @Autowired
    private TransactionTestFactory transactionTestFactory;

    private String accessToken;

    private Long userID;

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

        userID = userRepository.findByEmail("ivanov@example.com")
                .orElseThrow()
                .getId();

        cardTestFactory.createCard(
                "4486441729154030",
                userID,
                "ACTIVE",
                5000
        );

        cardTestFactory.createCard(
                "4024007123874108",
                userID,
                "ACTIVE",
                7500
        );

        transactionTestFactory.createTransaction(
                "4486441729154030",
                "4024007123874108",
                "TRANSFER",
                500,
                userID
        );

        transactionTestFactory.createTransaction(
                "4486441729154030",
                null,
                "WITHDRAWALS",
                1000,
                userID
        );

        transactionTestFactory.createTransaction(
                "4024007123874108",
                null,
                "WITHDRAWALS",
                100,
                userID
        );
    }

    @Test
    void getTransactions_ValidAdminAccess_ReturnsTransactionList() throws Exception {
        mockMvc.perform(get("/transactions")
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(3))
                .andExpect(jsonPath("$.transactions[0].maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("TRANSFER"))
                .andExpect(jsonPath("$.transactions[0].amount").value(500))
                .andExpect(jsonPath("$.transactions[2].maskedSource").value("4024****4108"))
                .andExpect(jsonPath("$.transactions[2].transactionType").value("WITHDRAWALS"))
                .andExpect(jsonPath("$.transactions[2].amount").value(100));
    }

    @Test
    void getTransactions_InvalidSortParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/transactions")
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
    void getUserTransactions_ValidUserAccess_ReturnsUserTransactions() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");
        mockMvc.perform(get("/transactions/my")
                        .header("Authorization", accessTokenUser)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(3))
                .andExpect(jsonPath("$.transactions[0].maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("TRANSFER"))
                .andExpect(jsonPath("$.transactions[0].amount").value(500))
                .andExpect(jsonPath("$.transactions[1].maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.transactions[1].transactionType").value("WITHDRAWALS"))
                .andExpect(jsonPath("$.transactions[1].amount").value(1000))
                .andExpect(jsonPath("$.transactions[2].maskedSource").value("4024****4108"))
                .andExpect(jsonPath("$.transactions[2].transactionType").value("WITHDRAWALS"))
                .andExpect(jsonPath("$.transactions[2].amount").value(100));
    }

    @Test
    void findById_ValidAdminAccess_ReturnsTransaction() throws Exception {
        mockMvc.perform(get("/transactions/{id}", 2L)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.transactionType").value("WITHDRAWALS"));
    }

    @Test
    void findById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/transactions/{id}", 999L)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Транзакция с ID 999 не найдена"));
    }

    @Test
    void createTransaction_ValidData_ReturnsCreatedTransaction() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var transactionDto = new TransactionCreateDto();
        transactionDto.setSourceNumber("4486441729154030");
        transactionDto.setDestinationNumber("4024007123874108");
        transactionDto.setTransactionType("TRANSFER");
        transactionDto.setAmount(1000);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.amount").value(1000));
    }

    @Test
    void createTransaction_InsufficientFunds_ReturnsPaymentRequired() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var transactionDto = new TransactionCreateDto();
        transactionDto.setSourceNumber("4486441729154030");
        transactionDto.setDestinationNumber("4024007123874108");
        transactionDto.setTransactionType("TRANSFER");
        transactionDto.setAmount(7000);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDto)))
                .andExpect(status().isPaymentRequired())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Недостаточно средств для совершения операции"));
    }

    @Test
    void createTransaction_fromCardNotBelongingUser_ReturnsNotFound() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var transactionDto = new TransactionCreateDto();
        transactionDto.setSourceNumber("4486441729154444");
        transactionDto.setDestinationNumber("4024007123874108");
        transactionDto.setTransactionType("TRANSFER");
        transactionDto.setAmount(1000);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Карта с номером 4486441729154444 не принадлежит пользователю с ID " + userID));
    }

    @Test
    void createTransaction_overdraftLimitWithdrawal_ReturnsForbidden() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var transactionDto = new TransactionCreateDto();
        transactionDto.setSourceNumber("4024007123874108");
        transactionDto.setDestinationNumber(null);
        transactionDto.setTransactionType("WITHDRAWALS");
        transactionDto.setAmount(5000);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDto)))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Превышен Суточный лимит на Снятие средств с карты"));
    }

    @Test
    void remove_ValidIdAsAdmin_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/transactions/{id}", 2L)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void remove_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/transactions/{id}", 999L)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Транзакция с ID 999 не найдена"));
    }

    @Test
    void getUserTransactions_ValidUserId_ReturnsTransactions() throws Exception {
        mockMvc.perform(get("/transactions/transactionsByUser/{userId}", userID)
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(3))
                .andExpect(jsonPath("$.transactions[0].amount").value(500))
                .andExpect(jsonPath("$.transactions[1].amount").value(1000))
                .andExpect(jsonPath("$.transactions[2].amount").value(100));
    }

    @Test
    void getUserTransactions_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/transactions/transactionsByUser/{userId}", 999L)
                        .header("Authorization", accessToken)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Пользователь с ID 999 не найден"));
    }

    @Test
    void getCardTransactions_ValidCard_ReturnsTransactions() throws Exception {
        var dto = new TransactionByCardDto();
        dto.setUserId(userID);
        dto.setCardLastFourDigits("4030");

        mockMvc.perform(post("/transactions/transactionsByCard")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2))
                .andExpect(jsonPath("$.transactions[0].maskedSource").value("4486****4030"))
                .andExpect(jsonPath("$.transactions[1].maskedSource").value("4486****4030"));
    }

    @Test
    void getCardTransactions_NonexistentCard_ReturnsEmptyList() throws Exception {
        var dto = new TransactionByCardDto();
        dto.setUserId(userID);
        dto.setCardLastFourDigits("9999");

        mockMvc.perform(post("/transactions/transactionsByCard")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions").isEmpty())
                .andExpect(jsonPath("$.transactions.length()").value(0));
    }

    @Test
    void getCardTransactions_InvalidLastFourDigits_ReturnsBadRequest() throws Exception {
        var dto = new TransactionByCardDto();
        dto.setUserId(userID);
        dto.setCardLastFourDigits("12");

        mockMvc.perform(post("/transactions/transactionsByCard")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].field")
                        .value("cardLastFourDigits"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Введите 4 последние цифры номера карты"));
    }

    @Test
    void getUserTransactionsByCard_ValidInput_ReturnsTransactions() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        mockMvc.perform(get("/transactions/my/byCard/{cardLastFourDigits}", "4108")
                        .header("Authorization", accessTokenUser)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "amount"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2))
                .andExpect(jsonPath("$.transactions[0].maskedSource").value("4024****4108"))
                .andExpect(jsonPath("$.transactions[0].amount").value(100))
                .andExpect(jsonPath("$.transactions[1].maskedDestination").value("4024****4108"))
                .andExpect(jsonPath("$.transactions[1].amount").value(500));
    }

    @Test
    void getUserTransactionsByCard_InvalidFormat_ReturnsBadRequest() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        mockMvc.perform(get("/transactions/my/byCard/{cardLastFourDigits}", "12a")
                        .header("Authorization", accessTokenUser)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].field").value("cardLastFourDigits"))
                .andExpect(jsonPath("$.errors[0].message").value("Введите последние четыре цифры карты."));
    }

    @Test
    void filterUserTransactions_ValidFilter_ReturnsFilteredTransactions() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var filter = new TransactionFilterDto();
        filter.setTransactionType("WITHDRAWALS");
        filter.setMinAmount(100);
        filter.setMaxAmount(1000);
        filter.setPage(1);
        filter.setSize(5);
        filter.setSortBy("amount");
        filter.setSortDirection("DESC");

        mockMvc.perform(post("/transactions/my/filter")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("WITHDRAWALS"))
                .andExpect(jsonPath("$.transactions[0].amount").value(1000))
                .andExpect(jsonPath("$.transactions[1].transactionType").value("WITHDRAWALS"))
                .andExpect(jsonPath("$.transactions[1].amount").value(100));
    }

    @Test
    void filterUserTransactions_InvalidAmountAndCreatedAfter_ReturnsBadRequest() throws Exception {
        var accessTokenUser = userTestFactory.getToken("ivanov@example.com");

        var filter = new TransactionFilterDto();
        filter.setMinAmount(-100);
        filter.setCreatedAfter("2025/01/01");
        filter.setPage(1);
        filter.setSize(5);

        mockMvc.perform(post("/transactions/my/filter")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[?(@.field == 'minAmount')].message")
                        .value("Введите положительное значение"))
                .andExpect(jsonPath("$.errors[?(@.field == 'createdAfter')].message")
                        .value("Некорректный формат даты. Ожидается yyyy-MM-dd."));
    }

    @Test
    void filterTransactions_ValidInput_ReturnsFilteredTransactions() throws Exception {
        var filterDto = new TransactionFilterDto();
        filterDto.setTransactionType("TRANSFER");
        filterDto.setMinAmount(100);

        var adminFilterDto = new TransactionAdminFilterDto();
        adminFilterDto.setUserId(userID);
        adminFilterDto.setTransactionFilterDto(filterDto);

        mockMvc.perform(post("/transactions/filter")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(adminFilterDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("TRANSFER"));
    }

    @Test
    void filterTransactions_InvalidFieldInFilter_ReturnsBadRequest() throws Exception {
        var filterDto = new TransactionFilterDto();
        filterDto.setSourceCardLastFour("12x");
        filterDto.setCreatedBefore("2025.02.02");

        var adminFilterDto = new TransactionAdminFilterDto();
        adminFilterDto.setUserId(userID);
        adminFilterDto.setTransactionFilterDto(filterDto);

        mockMvc.perform(post("/transactions/filter")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(adminFilterDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[*].field",
                        containsInAnyOrder("transactionFilterDto.createdBefore",
                                "transactionFilterDto.sourceCardLastFour")))
                .andExpect(jsonPath("$.errors[*].message",
                        containsInAnyOrder(
                                "Некорректный формат даты. Ожидается yyyy-MM-dd.",
                                "Введите 4 последние цифры номера карты")));
    }
}
