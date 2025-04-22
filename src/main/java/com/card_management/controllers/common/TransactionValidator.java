package com.card_management.controllers.common;

import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.service.CardService;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionFilterDto;
import com.card_management.transaction_api.enumeration.SortDirection;
import com.card_management.transaction_api.enumeration.TransactionSortFields;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Setter
@Getter
@Component
@RequiredArgsConstructor
public class TransactionValidator {

    private Card sourceEntity;
    private Card destinationEntity;
    private final UserService userService;
    private final CardService cardService;

    public void validateCreateTransaction(final TransactionCreateDto createDto) {
        if (createDto.getDestinationNumber() == null
                && createDto.getTransactionType().equals(TransactionType.TRANSFER.toString())) {
            throw new FieldsValidationException("Для перевода средств введите номер карты получателя.");
        }
        if (createDto.getDestinationNumber() != null
                && createDto.getTransactionType().equals(TransactionType.WITHDRAWALS.toString())) {
            throw new FieldsValidationException("Для снятия средств установите значение NULL в поле получателя.");
        }
        validTransactionType(createDto.getTransactionType());

        var user = userService.findById(createDto.getUserId());
        this.sourceEntity = cardService.findMatchByNumberCard(createDto.getSourceNumber(), user.getId());
        cardService.checkCardStatus(sourceEntity);
        if (createDto.getDestinationNumber() != null) {
            this.destinationEntity = cardService.findMatchByNumberCard(createDto.getDestinationNumber(), user.getId());
            cardService.checkCardStatus(destinationEntity);
        }
        if (createDto.getAmount() <= 0) {
            throw new FieldsValidationException("Для выполнения операции введите положительную сумму.");
        }
    }

    public void validateFilterTransaction(TransactionFilterDto filterDto) {
        if (filterDto.getTransactionType() != null) {
            validTransactionType(filterDto.getTransactionType());
        }
        if (filterDto.getSortBy() != null) {
            boolean isValidSortBy = Arrays.stream(TransactionSortFields.values())
                    .anyMatch(field -> field.getField().equals(filterDto.getSortBy()));
            if (!isValidSortBy) {
                throw new FieldsValidationException("Недопустимое поле сортировки: " + filterDto.getSortBy());
            }
        }
        if (filterDto.getSortDirection() != null) {
            if (!EnumUtils.isValidEnumIgnoreCase(SortDirection.class, filterDto.getSortDirection())) {
                throw new FieldsValidationException("Некорректное направление сортировки: "
                        + filterDto.getSortDirection());
            }
        }
    }

    private void validTransactionType(final String transactionType) {
        if (!EnumUtils.isValidEnumIgnoreCase(TransactionType.class, transactionType)) {
            throw new FieldsValidationException("Некорректное значение типа транзакции: " + transactionType);
        }
    }
}
