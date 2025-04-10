package com.card_management.limits_api.service;

import com.card_management.limits_api.dto.BalancesByLimitDto;
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
            throw new DuplicateLimitException("Лимит пользователю с ID " + userId + " назначен.");
        }
        var limit = limitMapper.map(limitDto);
        limitRepository.save(limit);
        return limitMapper.map(limit);
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

    public BalancesByLimitDto getBalancesByLimit(Long id) {
        var limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лимит с ID " + id + " не найден"));
        var balances = limit.getLimitAmount() - limit.getCurrentExpensesAmount();
        var balancesByLimit = limitMapper.mapBalances(limit);
        balancesByLimit.setBalances(balances);
        return balancesByLimit;
    }

    public boolean canTransaction(Limit limit, Integer amount) {
        checkAndResetLimits(limit);
        boolean transactionOk = (limit.getCurrentExpensesAmount() + amount) <= limit.getLimitAmount();
        if (!transactionOk) {
            throw new ExceedingLimitException("Превышен " + limit.getLimitType().getDescription() +
                    " на " + limit.getTransactionType().getDescription());
        }
        return true;
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
}
