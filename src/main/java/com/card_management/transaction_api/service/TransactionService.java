package com.card_management.transaction_api.service;

import com.card_management.cards_api.model.Card;
import com.card_management.controllers.common.TransactionValidator;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.transaction_api.dto.TransactionByCardDto;
import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionDto;
import com.card_management.transaction_api.dto.TransactionEnvelopDto;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.transaction_api.exception.InsufficientFundsForTransactionException;
import com.card_management.transaction_api.mapper.TransactionMapper;
import com.card_management.transaction_api.repository.TransactionRepository;
import com.card_management.users_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final UserRepository userRepository;

    private final LimitService limitService;

    private final LimitRepository limitRepository;

    private final TransactionValidator transactionValidator;

    public TransactionEnvelopDto getTransactions(int page, int size, String sort) {
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var transactionPage = transactionRepository.findAll(pageRequest);
        var transactionsDto = transactionPage.stream()
                .map(transactionMapper::map)
                .toList();
        return new TransactionEnvelopDto(
                transactionsDto,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages()
        );
    }

    public TransactionDto findById(Long id) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция с ID " + id + " не найдена"));
        return transactionMapper.map(transaction);
    }

    @Transactional
    public TransactionDto create(TransactionCreateDto transactionDto) {
        processingLimits(transactionDto);
        var sourceEntity = transactionValidator.getSourceEntity();
        checkBalancesCard(sourceEntity, transactionDto.getAmount());
        sourceEntity.setBalance(sourceEntity.getBalance() - transactionDto.getAmount());

        var transaction = transactionMapper.map(transactionDto);
        transaction.setSource(sourceEntity);
        sourceEntity.getOutgoingTransactions().add(transaction);
        if (transactionDto.getDestinationNumber() != null) {
            var destinationEntity = transactionValidator.getDestinationEntity();
            destinationEntity.setBalance(destinationEntity.getBalance() + transactionDto.getAmount());
            transaction.setDestination(destinationEntity);
            destinationEntity.getIncomingTransactions().add(transaction);
        }
        sourceEntity.getOwner().getTransactions().add(transaction);
        transactionRepository.save(transaction);
        return transactionMapper.map(transaction);
    }

    public void delete(Long id) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция с ID " + id + " не найдена"));
        transactionRepository.delete(transaction);
    }

    private void processingLimits(TransactionCreateDto transactionDto) {
        var transactionType = TransactionType.valueOf(transactionDto.getTransactionType());
        var limitDaly = limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(transactionDto.getUserId(),
                LimitType.DAILY, transactionType);
        var limitMonthly = limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(transactionDto.getUserId(),
                LimitType.MONTHLY, transactionType);

        limitService.canTransaction(limitDaly, transactionDto.getAmount());
        limitService.canTransaction(limitMonthly, transactionDto.getAmount());
        limitService.registerTransaction(limitDaly, transactionDto.getAmount());
        limitService.registerTransaction(limitMonthly, transactionDto.getAmount());
    }

    private void checkBalancesCard(Card card, Integer amount) {
        if (card.getBalance() < amount) {
            throw new InsufficientFundsForTransactionException("Недостаточно средств для совершения операции");
        }
    }

    public TransactionEnvelopDto getUserTransactions(Long userId, int page, int size, String sort) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + userId + " не найден"));
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var transactionPage = transactionRepository.findByUserId(userId, pageRequest);
        var transactionDto = transactionPage.stream()
                .map(transactionMapper::map)
                .toList();
        return new TransactionEnvelopDto(
                transactionDto,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages()
        );
    }

    public TransactionEnvelopDto getCardTransactions(TransactionByCardDto dto, int page, int size, String sort) {
        var userUuid = UUID.fromString(dto.getOwnerUuid());
        var userMaybe = userRepository.findByUuid(userUuid);
        if (userMaybe == null) {
            throw new ResourceNotFoundException("Пользователь с UUID " + dto.getOwnerUuid() + " не найден");
        }
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var transactionPage = transactionRepository
                .findByUserUuidAndCardLastFourDigits(userUuid, dto.getCardLastFourDigits(), pageRequest);
        var transactionDto = transactionPage.stream()
                .map(transactionMapper::map)
                .toList();
        return new TransactionEnvelopDto(
                transactionDto,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages()
        );
    }

    //todo filter
}
