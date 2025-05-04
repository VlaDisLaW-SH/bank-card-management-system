package com.card_management.transaction_api.service;

import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.service.CardService;
import com.card_management.controllers.common.TransactionValidator;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.exception.ExceedingLimitException;
import com.card_management.limits_api.model.Limit;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionDto;
import com.card_management.transaction_api.dto.TransactionEnvelopDto;
import com.card_management.transaction_api.dto.TransactionFilterDto;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.transaction_api.exception.InsufficientFundsForTransactionException;
import com.card_management.transaction_api.mapper.TransactionMapper;
import com.card_management.transaction_api.model.Transaction;
import com.card_management.transaction_api.repository.TransactionRepository;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.util.unit_test.CardTestFactory;
import com.card_management.util.unit_test.TransactionTestFactory;
import com.card_management.util.unit_test.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LimitService limitService;

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private TransactionValidator transactionValidator;

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;

    private Card card1;

    private Card card2;

    private Transaction transaction1;

    private Transaction transaction2;

    private TransactionDto transactionDto1;

    private TransactionDto transactionDto2;

    private TransactionCreateDto transactionCreateDto;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.createUser(1L);

        card1 = CardTestFactory.createCard(1L, user);
        card2 = CardTestFactory.createCard(2L, user);

        transactionCreateDto = new TransactionCreateDto();
        transactionCreateDto.setSourceNumber("1234567812341001");
        transactionCreateDto.setDestinationNumber("1234567812341002");
        transactionCreateDto.setTransactionType("TRANSFER");
        transactionCreateDto.setAmount(100);

        transaction1 = TransactionTestFactory.createTransaction(
                1L,
                user,
                card1,
                card2,
                TransactionType.TRANSFER,
                100
        );
        transactionDto1 = TransactionTestFactory.createTransactionDto(transaction1);

        transaction2 = TransactionTestFactory.createTransaction(
                2L,
                user,
                card1,
                card2,
                TransactionType.TRANSFER,
                500
        );
        transactionDto2 = TransactionTestFactory.createTransactionDto(transaction2);
    }

    @Test
    void getTransactions_validInput_returnsTransactionEnvelopDto() {
        var page = 1;
        var size = 2;
        var sort = "amount";

        var transactionList = List.of(transaction1, transaction2);
        var transactionPage = new PageImpl<>(
                transactionList,
                PageRequest.of(0, size, Sort.by(sort)), 1);

        when(transactionRepository.findAll(
                PageRequest.of(0, size, Sort.by(sort)))).thenReturn(transactionPage);
        when(transactionMapper.map(transaction1)).thenReturn(transactionDto1);
        when(transactionMapper.map(transaction2)).thenReturn(transactionDto2);

        TransactionEnvelopDto result = transactionService.getTransactions(page, size, sort);

        assertNotNull(result);
        assertEquals(2, result.getTransactions().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(transactionRepository, times(1))
                .findAll(PageRequest.of(0, size, Sort.by(sort)));
        verify(transactionMapper, times(1)).map(transaction1);
        verify(transactionMapper, times(1)).map(transaction2);
    }

    @Test
    void getTransactions_whenInvalidSortField_throwsFieldsValidationException() {
        int page = 1;
        int size = 2;
        var invalidSortField = "invalidField";

        var exception = assertThrows(
                FieldsValidationException.class,
                () -> transactionService.getTransactions(page, size, invalidSortField)
        );
        assertEquals("Недопустимое поле сортировки: " + invalidSortField, exception.getMessage());

        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void findById_existingId_returnsTransactionDto() {
        var transactionId = 1L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction1));
        when(transactionMapper.map(transaction1)).thenReturn(transactionDto1);

        var result = transactionService.findById(transactionId);

        assertNotNull(result);
        assertEquals(transactionDto1, result);

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionMapper, times(1)).map(transaction1);
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        var transactionId = 999L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () ->
                transactionService.findById(transactionId)
        );

        assertTrue(exception.getMessage().contains("Транзакция с ID " + transactionId + " не найдена"));

        verify(transactionRepository, times(1)).findById(transactionId);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void createTransaction_success() {
        when(transactionValidator.getSourceEntity()).thenReturn(card1);
        when(transactionValidator.getDestinationEntity()).thenReturn(card2);
        when(transactionMapper.map(transactionCreateDto, 1L)).thenReturn(transaction1);
        when(transactionMapper.map(any(Transaction.class))).thenReturn(transactionDto1);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction1);

        var result = transactionService.create(transactionCreateDto, 1L);

        assertNotNull(result);
        assertEquals("1234****1001", result.getMaskedSource());
        assertEquals("1234****1002", result.getMaskedDestination());
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertEquals(100, result.getAmount());

        verify(transactionRepository).save(transaction1);
    }

    @Test
    void createTransaction_shouldThrowException_whenInsufficientFunds() {
        transactionCreateDto.setAmount(2000);

        when(transactionValidator.getSourceEntity()).thenReturn(card1);

        var exception = assertThrows(InsufficientFundsForTransactionException.class, () ->
                transactionService.create(transactionCreateDto, 1L)
        );

        assertEquals("Недостаточно средств для совершения операции", exception.getMessage());
        verify(transactionMapper, never()).map(any(Transaction.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_exceedsLimit_throwsExceedingLimitException() {
        Limit exceededDailyLimit = new Limit();
        exceededDailyLimit.setLimitType(LimitType.DAILY);
        exceededDailyLimit.setTransactionType(TransactionType.TRANSFER);
        exceededDailyLimit.setLimitAmount(500);
        exceededDailyLimit.setCurrentExpensesAmount(400);

        Limit monthlyLimit = new Limit();
        monthlyLimit.setLimitType(LimitType.MONTHLY);
        monthlyLimit.setTransactionType(TransactionType.TRANSFER);
        monthlyLimit.setLimitAmount(10000);
        monthlyLimit.setCurrentExpensesAmount(1000);

        transactionCreateDto.setAmount(1000);

        when(limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(user.getId(),
                LimitType.DAILY, TransactionType.TRANSFER)).thenReturn(exceededDailyLimit);
        when(limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(user.getId(),
                LimitType.MONTHLY, TransactionType.TRANSFER)).thenReturn(monthlyLimit);

        doThrow(new ExceedingLimitException("Превышен дневной лимит"))
                .when(limitService).canTransaction(eq(exceededDailyLimit), eq(1000));

        assertThrows(ExceedingLimitException.class,
                () -> transactionService.create(transactionCreateDto, user.getId()));

        verify(limitRepository, times(1))
                .getLimitByUserIdAndLimitTypeAndTransactionType(user.getId(),
                LimitType.DAILY, TransactionType.TRANSFER);
        verify(limitRepository, times(1))
                .getLimitByUserIdAndLimitTypeAndTransactionType(user.getId(),
                LimitType.MONTHLY, TransactionType.TRANSFER);

        verify(limitService, times(1)).canTransaction(eq(exceededDailyLimit), eq(1000));
        verify(limitService, never()).registerTransaction(any(), anyInt());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void delete_existingTransaction_deletesSuccessfully() {
        var transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction1));

        transactionService.delete(transactionId);

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).delete(transaction1);
    }

    @Test
    void delete_nonExistingTransaction_throwsResourceNotFoundException() {
        Long transactionId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.delete(transactionId));

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void getUserTransactions_existingUser_returnsTransactionEnvelopDto() {
        var userId = 1L;
        var page = 1;
        var size = 10;
        var sort = "amount";
        var transactionPage = new PageImpl<>(List.of(transaction1, transaction2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUserId(
                userId,
                PageRequest.of(page - 1, size, Sort.by(sort))))
                .thenReturn(transactionPage);
        when(transactionMapper.map(transaction1)).thenReturn(transactionDto1);
        when(transactionMapper.map(transaction2)).thenReturn(transactionDto2);

        TransactionEnvelopDto result = transactionService.getUserTransactions(userId, page, size, sort);

        assertNotNull(result);
        assertEquals(2, result.getTransactions().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, times(1))
                .findByUserId(userId, PageRequest.of(page - 1, size, Sort.by(sort)));
    }

    @Test
    void getUserTransactions_userNotFound_throwsResourceNotFoundException() {
        var userId = 999L;
        var page = 1;
        var size = 10;
        var sort = "amount";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getUserTransactions(userId, page, size, sort));

        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, never()).findByUserId(anyLong(), any(PageRequest.class));
    }

    @Test
    void getUserTransactionsByCard_validInput_returnsFilteredTransactions() {
        var userId = 1L;
        var lastFour = "1001";
        var maskNumber = "1234****1001";
        var page = 1;
        var size = 10;
        var sort = "amount";

        var pageResult = new PageImpl<>(List.of(transaction1, transaction2));

        when(transactionRepository.findByUserId(userId, PageRequest.of(0, size, Sort.by(sort))))
                .thenReturn(pageResult);

        when(cardService.getCardLastFourDigits(transaction1.getSource())).thenReturn("1001");
        when(cardService.getCardLastFourDigits(transaction2.getSource())).thenReturn("1001");
        when(cardService.getCardLastFourDigits(transaction2.getDestination())).thenReturn("1002");

        when(transactionMapper.map(transaction1)).thenReturn(transactionDto1);
        when(transactionMapper.map(transaction2)).thenReturn(transactionDto2);

        TransactionEnvelopDto result = transactionService
                .getUserTransactionsByCard(userId, lastFour, page, size, sort);

        assertNotNull(result);
        assertEquals(maskNumber, result.getTransactions().get(0).getMaskedSource());
        assertEquals(maskNumber, result.getTransactions().get(1).getMaskedSource());
        assertEquals(2, result.getTransactions().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(transactionRepository, times(1))
                .findByUserId(userId, PageRequest.of(0, size, Sort.by(sort)));
    }

    @Test
    void getUserTransactionsByCard_noMatches_returnsEmptyResult() {
        var userId = 1L;
        var lastFour = "9999";
        var page = 1;
        var size = 10;
        var sort = "amount";

        var pageResult = new PageImpl<>(List.of(transaction1));

        when(transactionRepository.findByUserId(userId, PageRequest.of(0, size, Sort.by(sort))))
                .thenReturn(pageResult);

        when(cardService.getCardLastFourDigits(transaction1.getSource())).thenReturn("1001");
        when(cardService.getCardLastFourDigits(transaction1.getDestination())).thenReturn("1002");

        TransactionEnvelopDto result = transactionService
                .getUserTransactionsByCard(userId, lastFour, page, size, sort);

        assertNotNull(result);
        assertEquals(0, result.getTransactions().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(transactionRepository, times(1))
                .findByUserId(userId, PageRequest.of(0, size, Sort.by(sort)));
    }

    @Test
    void filterTransactions_validFilter_returnsFilteredTransactions() {
        var filterDto = new TransactionFilterDto();
        filterDto.setPage(1);
        filterDto.setSize(10);
        filterDto.setSortBy("amount");
        filterDto.setSortDirection("DESC");
        filterDto.setTransactionType("TRANSFER");

        var userId = 1L;
        var page = new PageImpl<>(List.of(transaction1, transaction2));
        var expectedPageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "amount")
        );

        when(transactionRepository.findAll(ArgumentMatchers.<Specification<Transaction>>any(), eq(expectedPageable)))
                .thenReturn(page);
        when(transactionMapper.map(transaction1)).thenReturn(transactionDto1);
        when(transactionMapper.map(transaction2)).thenReturn(transactionDto2);

        TransactionEnvelopDto result = transactionService.filterTransactions(filterDto, userId);

        assertNotNull(result);
        assertEquals(2, result.getTransactions().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(transactionRepository).findAll(ArgumentMatchers.<Specification<Transaction>>any(), eq(expectedPageable));
        verify(transactionMapper).map(transaction1);
        verify(transactionMapper).map(transaction2);
    }

    @Test
    void filterTransactions_noMatches_returnsEmptyResult() {
        var filterDto = new TransactionFilterDto();
        filterDto.setPage(1);
        filterDto.setSize(5);
        filterDto.setSortBy("createdAt");
        filterDto.setSortDirection("DESC");

        var userId = 1L;
        Page<Transaction> emptyPage = new PageImpl<>(List.of());
        var expectedPageable = PageRequest.of(
                0,
                5,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        when(transactionRepository.findAll(ArgumentMatchers.<Specification<Transaction>>any(), eq(expectedPageable)))
                .thenReturn(emptyPage);

        TransactionEnvelopDto result = transactionService.filterTransactions(filterDto, userId);

        assertNotNull(result);
        assertTrue(result.getTransactions().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(transactionRepository).findAll(ArgumentMatchers.<Specification<Transaction>>any(), eq(expectedPageable));
        verify(transactionMapper, never()).map(any());
    }
}
