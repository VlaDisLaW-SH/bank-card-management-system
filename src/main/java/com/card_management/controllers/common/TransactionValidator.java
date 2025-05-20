package com.card_management.controllers.common;

import com.card_management.technical.enumeration.FieldEnumerable;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionFilterDto;
import com.card_management.technical.enumeration.SortDirection;
import com.card_management.transaction_api.enumeration.TransactionSortFields;
import com.card_management.transaction_api.enumeration.TransactionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionValidator {

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
    }

    public void validateFilterTransaction(TransactionFilterDto filterDto) {
        if (filterDto.getTransactionType() != null) {
            validTransactionType(filterDto.getTransactionType());
        }
        if (filterDto.getSortBy() != null) {
            validSortFields(filterDto.getSortBy());
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

    public void validSortFields(final String sort) {
        if (!FieldEnumerable.containsField(TransactionSortFields.class, sort)) {
            throw new FieldsValidationException("Недопустимое поле сортировки: " + sort);
        }
    }
}
