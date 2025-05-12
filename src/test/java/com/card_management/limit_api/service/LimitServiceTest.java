package com.card_management.limit_api.service;

import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.dto.LimitEnvelopDto;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.exception.DuplicateLimitException;
import com.card_management.limits_api.exception.ExceedingLimitException;
import com.card_management.limits_api.mapper.LimitMapper;
import com.card_management.limits_api.model.Limit;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.factory.unit.LimitTestFactory;
import com.card_management.factory.unit.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LimitServiceTest {
    @Mock
    private LimitRepository limitRepository;

    @Mock
    private LimitMapper limitMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LimitService limitService;

    private Limit limit1;

    private Limit limit2;

    private LimitDto limitDto1;

    private LimitDto limitDto2;

    private LimitCreateDto limitCreateDto;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.createUser(1L);

        limitCreateDto = LimitCreateDto.builder()
                .userId(1L)
                .limitType("DAILY")
                .transactionType("TRANSFER")
                .limitAmount(1000)
                .build();

        limit1 = LimitTestFactory.createLimit(
                1L,
                user,
                LimitType.DAILY,
                TransactionType.TRANSFER,
                1000
        );
        limitDto1 = LimitTestFactory.createLimitDto(limit1);

        limit2 = LimitTestFactory.createLimit(
                2L,
                user,
                LimitType.MONTHLY,
                TransactionType.TRANSFER,
                50000
        );
        limitDto2 = LimitTestFactory.createLimitDto(limit2);
    }

    @Test
    void getLimits_whenValidParams_returnsLimitEnvelopDto() {
        var page = 1;
        var size = 2;
        var sort = "balances";

        var limits = List.of(limit1, limit2);
        var limitPage = new PageImpl<>(limits, PageRequest.of(0, 2, Sort.by(sort)), 2);

        when(limitRepository.findAll(PageRequest.of(0, 2, Sort.by(sort)))).thenReturn(limitPage);
        when(limitMapper.map(limit1)).thenReturn(limitDto1);
        when(limitMapper.map(limit2)).thenReturn(limitDto2);

        LimitEnvelopDto result = limitService.getLimits(page, size, sort);

        assertNotNull(result);
        assertEquals(2, result.getLimits().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getLimits_whenInvalidSortField_throwsFieldsValidationException() {
        var invalidSortField = "invalidField";
        var page = 0;
        var size = 10;

        var exception = assertThrows(
                FieldsValidationException.class,
                () -> limitService.getLimits(page, size, invalidSortField)
        );
        assertEquals("Недопустимое поле сортировки: " + invalidSortField, exception.getMessage());
    }

    @Test
    void findById_whenLimitExists_returnsLimitDto() {
        var id = 1L;

        when(limitRepository.findById(id)).thenReturn(Optional.of(limit1));
        when(limitMapper.map(limit1)).thenReturn(limitDto1);

        var result = limitService.findById(id);

        assertEquals(limitDto1, result);
        verify(limitRepository).findById(id);
        verify(limitMapper).map(limit1);
    }

    @Test
    void findById_whenLimitDoesNotExist_throwsResourceNotFoundException() {
        var id = 1L;
        when(limitRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ResourceNotFoundException.class,
                () -> limitService.findById(id)
        );
        assertEquals("Лимит с ID " + id + " не найден", exception.getMessage());
        verify(limitRepository).findById(id);
        verifyNoMoreInteractions(limitMapper);
    }

    @Test
    void createLimit_successfulCreation_returnsLimitDto() {
        when(limitRepository.existsByUserIdAndLimitTypeAndTransactionType(
                1L, LimitType.DAILY, TransactionType.TRANSFER)).thenReturn(false);
        when(limitMapper.map(limitCreateDto)).thenReturn(limit1);
        when(limitMapper.map(limit1)).thenReturn(limitDto1);

        var result = limitService.create(limitCreateDto);

        assertNotNull(result);
        assertEquals(LimitType.DAILY, result.getLimitType());
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertEquals(1000, result.getLimitAmount());

        verify(limitRepository).save(limit1);
    }

    @Test
    void createLimit_duplicateLimit_throwsException() {
        when(limitRepository.existsByUserIdAndLimitTypeAndTransactionType(
                1L, LimitType.DAILY, TransactionType.TRANSFER)).thenReturn(true);

        var exception = assertThrows(DuplicateLimitException.class, () -> {
            limitService.create(limitCreateDto);
        });
        assertTrue(exception.getMessage().contains("Лимит пользователю с ID 1 назначен"));

        verify(limitRepository, never()).save(any());
        verify(limitMapper, never()).map(any(LimitCreateDto.class));
    }

    @Test
    void setLimit_whenActiveLimitExists_updatesLimitAndReturnsConfirmationMessage() {
        var dto = LimitCreateDto.builder()
                .userId(1L)
                .limitType("DAILY")
                .transactionType("TRANSFER")
                .limitAmount(5000)
                .build();

        when(limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(
                1L, LimitType.DAILY, TransactionType.TRANSFER)).thenReturn(limit1);

        var result = limitService.setLimit(dto);

        assertEquals("Лимит будет применен в новый расчетный период.", result);
        assertEquals(5000, limit1.getPendingLimitAmount());
        assertTrue(limit1.getHasPendingUpdate());
        verify(limitRepository).save(limit1);
    }

    @Test
    void setLimit_whenActiveLimitNotFound_throwsResourceNotFoundException() {
        when(limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(
                1L, LimitType.DAILY, TransactionType.TRANSFER)).thenReturn(null);

        var exception = assertThrows(
                ResourceNotFoundException.class,
                () -> limitService.setLimit(limitCreateDto)
        );

        var expectedMessage = "Суточный лимит для типа транзакции Перевод средств с карты не установлен " +
                "пользователю с ID 1" + System.lineSeparator() + "Необходимо создать новый лимит данного типа.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(limitRepository, never()).save(any());
    }

    @Test
    void delete_whenLimitExists_deletesLimit() {
        var limitId = 1L;
        when(limitRepository.findById(limitId)).thenReturn(Optional.of(limit1));
        limitService.delete(limitId);
        verify(limitRepository).delete(limit1);
    }

    @Test
    void delete_whenLimitNotFound_throwsResourceNotFoundException() {
        var limitId = 1L;
        when(limitRepository.findById(limitId)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ResourceNotFoundException.class,
                () -> limitService.delete(limitId)
        );
        assertEquals("Лимит с ID 1 не найден", exception.getMessage());
        verify(limitRepository, never()).delete(any());
    }

    @Test
    void getUserLimits_whenUserExists_returnsLimitList() {
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(limitRepository.getLimitsByUserId(userId)).thenReturn(List.of(limit1, limit2));
        when(limitMapper.map(limit1)).thenReturn(limitDto1);
        when(limitMapper.map(limit2)).thenReturn(limitDto2);

        var result = limitService.getUserLimits(userId);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(limitDto1, limitDto2)));
    }

    @Test
    void getUserLimits_whenUserNotFound_throwsResourceNotFoundException() {
        var userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ResourceNotFoundException.class,
                () -> limitService.getUserLimits(userId)
        );

        assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
        verify(limitRepository, never()).getLimitsByUserId(any());
        verifyNoInteractions(limitMapper);
    }

    @Test
    void resetLimitsByType_whenLimitsExist_resetValuesAndSave() {
        var limitType = LimitType.MONTHLY;
        var limit1 = mock(Limit.class);
        var limit2 = mock(Limit.class);
        var limits = List.of(limit1, limit2);

        when(limitRepository.findByLimitType(limitType)).thenReturn(limits);

        limitService.resetLimitsByType(limitType);

        for (Limit limit : limits) {
            verify(limit).setCurrentExpensesAmount(0);
            verify(limit).setDateLastTransaction(null);
            verify(limitRepository).save(limit);
        }
    }

    @Test
    void resetLimitsByType_whenNoLimitsFound_doesNothing() {
        var limitType = LimitType.DAILY;
        when(limitRepository.findByLimitType(limitType)).thenReturn(Collections.emptyList());

        limitService.resetLimitsByType(limitType);

        verify(limitRepository, never()).save(any());
    }

    @Test
    void canTransaction_whenAmountWithinLimit_doesNotThrow() {
        limit1.setCurrentExpensesAmount(500);
        assertDoesNotThrow(() -> limitService.canTransaction(limit1, 400));
    }

    @Test
    void canTransaction_whenAmountExceedsLimit_throwsException() {
        limit1.setCurrentExpensesAmount(900);
        limit1.setDateLastTransaction(LocalDate.now());

        var ex = assertThrows(ExceedingLimitException.class, () ->
                limitService.canTransaction(limit1, 200));

        assertEquals("Превышен Суточный лимит на Перевод средств с карты", ex.getMessage());
    }

    @Test
    void registerTransaction_whenValidInput_updatesLimitAndSaves() {
        limit1.setCurrentExpensesAmount(200);
        limit1.setDateLastTransaction(LocalDate.now());
        var amount = 300;

        limitService.registerTransaction(limit1, amount);

        assertEquals(500, limit1.getCurrentExpensesAmount());
        assertEquals(LocalDate.now(), limit1.getDateLastTransaction());
        verify(limitRepository).save(limit1);
    }

    @Test
    void registerTransaction_whenLimitsWereReset_registersFromZero() {
        limit1.setCurrentExpensesAmount(0);
        limit1.setDateLastTransaction(null);
        var amount = 400;

        limitService.registerTransaction(limit1, amount);

        assertEquals(400, limit1.getCurrentExpensesAmount());
        assertEquals(LocalDate.now(), limit1.getDateLastTransaction());
        verify(limitRepository).save(limit1);
    }

    @Test
    void setDefaultLimits_whenCalled_savesFourDefaultLimits() {
        limitService.setDefaultLimits(user);

        ArgumentCaptor<Limit> captor = ArgumentCaptor.forClass(Limit.class);
        verify(limitRepository, times(4)).save(captor.capture());

        List<Limit> savedLimits = captor.getAllValues();

        assertThat(savedLimits)
                .extracting(Limit::getLimitType, Limit::getTransactionType, Limit::getLimitAmount)
                .containsExactlyInAnyOrder(
                        tuple(LimitType.DAILY, TransactionType.TRANSFER, 10000),
                        tuple(LimitType.MONTHLY, TransactionType.TRANSFER, 150000),
                        tuple(LimitType.DAILY, TransactionType.WITHDRAWALS, 5000),
                        tuple(LimitType.MONTHLY, TransactionType.WITHDRAWALS, 50000)
                );

        savedLimits.forEach(limit -> assertEquals(user, limit.getUser()));
    }
}
