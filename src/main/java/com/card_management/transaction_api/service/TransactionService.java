package com.card_management.transaction_api.service;

import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.service.CardService;
import com.card_management.controllers.common.TransactionValidator;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.limits_api.service.LimitService;
import com.card_management.technical.exception.ResourceNotFoundException;
import com.card_management.transaction_api.dto.*;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.transaction_api.exception.InsufficientFundsForTransactionException;
import com.card_management.transaction_api.mapper.TransactionMapper;
import com.card_management.transaction_api.repository.TransactionRepository;
import com.card_management.transaction_api.specification.TransactionSpecifications;
import com.card_management.users_api.repository.UserRepository;
import com.card_management.users_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static com.card_management.technical.util.PaginationUtils.createPageable;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final UserRepository userRepository;

    private final UserService userService;

    private final LimitService limitService;

    private final LimitRepository limitRepository;

    private final TransactionValidator transactionValidator;

    private final CardService cardService;

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
    public TransactionDto create(TransactionCreateDto transactionDto, Long userId) {
        processingLimits(transactionDto, userId);
        var sourceEntity = transactionValidator.getSourceEntity();
        checkBalancesCard(sourceEntity, transactionDto.getAmount());
        sourceEntity.setBalance(sourceEntity.getBalance() - transactionDto.getAmount());

        var transaction = transactionMapper.map(transactionDto, userId);
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

    private void processingLimits(TransactionCreateDto transactionDto, Long userId) {
        var transactionType = TransactionType.valueOf(transactionDto.getTransactionType());
        var limitDaly = limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(userId,
                LimitType.DAILY, transactionType);
        var limitMonthly = limitRepository.getLimitByUserIdAndLimitTypeAndTransactionType(userId,
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
        var userMaybe = userService.findById(dto.getUserId());
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var transactionPage = transactionRepository
                .findByUserId(userMaybe.getId(), pageRequest);
        var transactionsBySource = transactionPage.stream()
                .filter(card -> cardService.getCardLastFourDigits(card.getSource()).equals(dto.getCardLastFourDigits()))
                .map(transactionMapper::map)
                .toList();
        var transactionsByDestination = transactionPage.stream()
                .filter(card -> card.getDestination() != null
                        && cardService.getCardLastFourDigits(card.getDestination()).equals(dto.getCardLastFourDigits()))
                .map(transactionMapper::map)
                .toList();
        var transactionsList = Stream.concat(
                transactionsBySource.stream(),
                transactionsByDestination.stream()
                )
                .toList();
        return new TransactionEnvelopDto(
                transactionsList,
                transactionsList.size(), //check cast
                transactionPage.getTotalPages()
        );
    }

    public TransactionEnvelopDto getUserTransactionsByCard(
            Long userId,
            String cardLastFourDigits,
            int page,
            int size,
            String sort
    ) {
        var pageRequest = PageRequest.of(page -1, size, Sort.by(sort));
        var transactionPage = transactionRepository
                .findByUserId(userId, pageRequest);
        var transactionsBySource = transactionPage.stream()
                .filter(card -> cardService.getCardLastFourDigits(card.getSource()).equals(cardLastFourDigits))
                .map(transactionMapper::map)
                .toList();
        var transactionsByDestination = transactionPage.stream()
                .filter(card -> card.getDestination() != null
                        && cardService.getCardLastFourDigits(card.getDestination()).equals(cardLastFourDigits))
                .map(transactionMapper::map)
                .toList();
        var transactionsList = Stream.concat(
                        transactionsBySource.stream(),
                        transactionsByDestination.stream()
                )
                .toList();
        return new TransactionEnvelopDto(
                transactionsList,
                transactionsList.size(), //check cast
                transactionPage.getTotalPages()
        );
    }

    public TransactionEnvelopDto getFilteredTransactions(TransactionAdminFilterDto adminFilterDtoDto) {
        Long userId = null;
        if (adminFilterDtoDto.getUserId() != null) {
            var user = userService.findById(adminFilterDtoDto.getUserId());
            userId = user.getId();
        }
        var filterDto = adminFilterDtoDto.getTransactionFilterDto();
        var pageable = createPageable(
                filterDto,
                TransactionFilterDto::getSortBy,
                TransactionFilterDto::getSortDirection,
                TransactionFilterDto::getPage,
                TransactionFilterDto::getSize,
                "createdAt",
                "DESC"
        );
        var transactionsPage = transactionRepository.findAll(
                TransactionSpecifications.withFilter(filterDto, userId), pageable
        );
        var transactionDtoList = transactionsPage.stream()
                .map(transactionMapper::map)
                .toList();
        return new TransactionEnvelopDto(
                transactionDtoList,
                transactionsPage.getTotalElements(),
                transactionsPage.getTotalPages()
        );
    }

    public TransactionEnvelopDto filterUserTransactions(Long userId, TransactionFilterDto filterDto) {
        var pageable = createPageable(
                filterDto,
                TransactionFilterDto::getSortBy,
                TransactionFilterDto::getSortDirection,
                TransactionFilterDto::getPage,
                TransactionFilterDto::getSize,
                "createdAt",
                "DESC"
        );
        var transactionsPage = transactionRepository.findAll(
                TransactionSpecifications.withFilter(filterDto, userId), pageable
        );
        var transactionDtoList = transactionsPage.stream()
                .map(transactionMapper::map)
                .toList();
        return new TransactionEnvelopDto(
                transactionDtoList,
                transactionsPage.getTotalElements(),
                transactionsPage.getTotalPages()
        );
    }
}
