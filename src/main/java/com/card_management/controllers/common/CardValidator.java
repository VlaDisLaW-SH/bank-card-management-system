package com.card_management.controllers.common;

import com.card_management.cards_api.dto.CardChangeStatusDto;
import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.enumeration.CardSortFields;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.technical.enumeration.FieldEnumerable;
import com.card_management.technical.exception.FieldsValidationException;
import com.card_management.transaction_api.enumeration.SortDirection;
import com.card_management.users_api.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@RequiredArgsConstructor
public class CardValidator {

    private final UserService userService;

    public void validateCreateCard(final CardCreateDto createDto) {
        validCardStatus(createDto.getStatus());
    }

    public void validateSetCardStatus(final CardChangeStatusDto cardChangeStatusDto) {
        userService.findById(cardChangeStatusDto.getOwnerId());
        validCardStatus(cardChangeStatusDto.getStatus());
    }

    public void validateFilterCard(CardFilterDto cardFilterDto) {
        if (cardFilterDto.getStatus() != null){
            validCardStatus(cardFilterDto.getStatus());
        }
        if (cardFilterDto.getSortBy() != null) {
            validSortFields(cardFilterDto.getSortBy());
        }
        if (cardFilterDto.getSortDirection() != null) {
            if (!EnumUtils.isValidEnumIgnoreCase(SortDirection.class, cardFilterDto.getSortDirection())) {
                throw new FieldsValidationException("Некорректное направление сортировки: "
                        + cardFilterDto.getSortDirection());
            }
        }
    }

    private void validCardStatus(final String cardStatus) {
        if (!EnumUtils.isValidEnumIgnoreCase(CardStatus.class, cardStatus)) {
            throw new FieldsValidationException("Некорректное значение статуса карты: " + cardStatus);
        }
    }

    public void validSortFields(final String sort) {
        if (!FieldEnumerable.containsField(CardSortFields.class, sort)) {
            throw new FieldsValidationException("Недопустимое поле сортировки: " + sort);
        }
    }
}
