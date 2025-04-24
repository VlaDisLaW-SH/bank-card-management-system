package com.card_management.controllers.common;

import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LimitValidator {

    private final UserService userService;

    public void validateCreateLimit(final LimitCreateDto createDto) {
        userService.findById(createDto.getUserId());

        if (!EnumUtils.isValidEnumIgnoreCase(LimitType.class, createDto.getLimitType())) {
            throw new FieldsValidationException("Недопустимое значение типа лимита: "
                    + createDto.getLimitType());
        }
        if (!EnumUtils.isValidEnumIgnoreCase(TransactionType.class, createDto.getTransactionType())) {
            throw new FieldsValidationException("Недопустимое значение типа транзакции: "
                    + createDto.getTransactionType());
        }
    }
}
