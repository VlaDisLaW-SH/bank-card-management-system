package com.card_management.limits_api.service;

import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.dto.LimitEnvelopDto;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.exception.DuplicateLimitException;
import com.card_management.limits_api.exception.ExceedingLimitException;
import com.card_management.limits_api.mapper.LimitMapper;
import com.card_management.limits_api.model.Limit;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitService {

    private final LimitRepository limitRepository;

    private final LimitMapper limitMapper;

    private final UserRepository userRepository;

    public LimitEnvelopDto getLimits(int page, int size, String sort) {
        var pageRequest = PageRequest.of(page - 1, size, Sort.by(sort));
        var limitPage = limitRepository.findAll(pageRequest);
        var limitDto = limitPage.stream()
                .map(limitMapper::map)
                .toList();
        return new LimitEnvelopDto(
                limitDto,
                limitPage.getTotalElements(),
                limitPage.getTotalPages()
        );
    }

    public LimitDto findById(Long id) {
        var limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лимит с ID " + id + " не найден"));
        return limitMapper.map(limit);
    }

    public LimitDto create(LimitCreateDto limitDto) {
        var userId = limitDto.getUserId();
        var limitType = LimitType.valueOf(limitDto.getLimitType().toUpperCase());
        var transactionType = TransactionType.valueOf(limitDto.getTransactionType().toUpperCase());
        if (limitRepository.existsByUserIdAndLimitTypeAndTransactionType(userId, limitType, transactionType)) {
            throw new DuplicateLimitException("Лимит пользователю с ID " + userId + " назначен."
                    + System.lineSeparator() + "Обновите лимит данного типа или удалите его.");
        }
        var limit = limitMapper.map(limitDto);
        limitRepository.save(limit);
        return limitMapper.map(limit);
    }

    public String setLimit(LimitCreateDto limitDto) {
        var userId = limitDto.getUserId();
        var limitType = LimitType.valueOf(limitDto.getLimitType().toUpperCase());
        var transactionType = TransactionType.valueOf(limitDto.getTransactionType().toUpperCase());
        var activeLimit = limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(
                userId,
                limitType,
                transactionType);
        if (activeLimit == null) {
            throw new ResourceNotFoundException(limitType.getDescription() + " для типа транзакции "
                    + transactionType.getDescription() + " не установлен пользователю с ID " + userId
                    + System.lineSeparator() + "Необходимо создать новый лимит данного типа.");
        }
        activeLimit.setPendingLimitAmount(limitDto.getLimitAmount());
        activeLimit.setHasPendingUpdate(true);
        limitRepository.save(activeLimit);
        return "Лимит будет применен в новый расчетный период.";
    }

    public void delete(Long id) {
        var limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лимит с ID " + id + " не найден"));
        limitRepository.delete(limit);
    }

    public List<LimitDto> getUserLimits(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + userId + " не найден"));
        var limitsList = limitRepository.getLimitsByUserId(userId);
        return limitsList.stream()
                .map(limitMapper::map)
                .toList();
    }

    public void resetLimitsByType(LimitType limitType) {
        List<Limit> limits = limitRepository.findByLimitType(limitType);

        for (Limit limit : limits) {
            limit.setCurrentExpensesAmount(0);
            limit.setDateLastTransaction(null);
            limitRepository.save(limit);
        }
    }

    public void canTransaction(Limit limit, Integer amount) {
        checkAndResetLimits(limit);
        boolean transactionOk = (limit.getCurrentExpensesAmount() + amount) <= limit.getLimitAmount();
        if (!transactionOk) {
            throw new ExceedingLimitException("Превышен " + limit.getLimitType().getDescription() +
                    " на " + limit.getTransactionType().getDescription());
        }
    }

    public void registerTransaction(Limit limit, Integer amount) {
        checkAndResetLimits(limit);
        limit.setCurrentExpensesAmount(limit.getCurrentExpensesAmount() + amount);
        limit.setDateLastTransaction(LocalDate.now());
        limitRepository.save(limit);
    }

    private void checkAndResetLimits(Limit limit) {
        LocalDate currentDate = LocalDate.now();

        if (limit.getLimitType().toString().equals("DAILY")) {
            if (limit.getDateLastTransaction() == null || !limit.getDateLastTransaction().equals(currentDate)) {
                limit.setCurrentExpensesAmount(0);
                limit.setDateLastTransaction(null);
            }
        }
        if (limit.getLimitType().toString().equals("MONTHLY")) {
            if (limit.getDateLastTransaction() == null
                    || !limit.getDateLastTransaction().getMonth().equals(currentDate.getMonth())
            ) {
                limit.setCurrentExpensesAmount(0);
                limit.setDateLastTransaction(null);
            }
        }
    }

    public void setDefaultLimits(User user) {
        var dailyTransfer = new Limit();
        dailyTransfer.setUser(user);
        dailyTransfer.setLimitType(LimitType.DAILY);
        dailyTransfer.setTransactionType(TransactionType.TRANSFER);
        dailyTransfer.setLimitAmount(10000);
        limitRepository.save(dailyTransfer);

        var monthlyTransfer = new Limit();
        monthlyTransfer.setUser(user);
        monthlyTransfer.setLimitType(LimitType.MONTHLY);
        monthlyTransfer.setTransactionType(TransactionType.TRANSFER);
        monthlyTransfer.setLimitAmount(150000);
        limitRepository.save(monthlyTransfer);

        var dailyWithdrawals = new Limit();
        dailyWithdrawals.setUser(user);
        dailyWithdrawals.setLimitType(LimitType.DAILY);
        dailyWithdrawals.setTransactionType(TransactionType.WITHDRAWALS);
        dailyWithdrawals.setLimitAmount(5000);
        limitRepository.save(dailyWithdrawals);

        var monthlyWithdrawals = new Limit();
        monthlyWithdrawals.setUser(user);
        monthlyWithdrawals.setLimitType(LimitType.MONTHLY);
        monthlyWithdrawals.setTransactionType(TransactionType.WITHDRAWALS);
        monthlyWithdrawals.setLimitAmount(50000);
        limitRepository.save(monthlyWithdrawals);
    }
}
