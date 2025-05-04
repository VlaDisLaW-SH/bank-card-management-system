package com.card_management.util.unit_test;

import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.model.Card;
import com.card_management.users_api.model.User;

public class CardTestFactory {

    public static Card createCard(Long id, User owner) {
        Card card = new Card();
        card.setId(id);
        card.setEncryptedCardNumber("encrypted" + id);
        card.setMaskNumber("1234****" + (1000 + id));
        card.setOwner(owner);
        card.setValidityPeriodMonth(12);
        card.setValidityPeriodYear(30);
        card.setStatus(CardStatus.ACTIVE);
        card.setSaltNumberCard("salt-" + id);
        card.setBalance(1000);
        return card;
    }

    public static CardDto createCardDto(Long id) {
        CardDto dto = new CardDto();
        dto.setId(id);
        dto.setMaskNumber("1234****" + (1000 + id));
        dto.setStatus(CardStatus.ACTIVE);
        dto.setBalance(1000);
        return dto;
    }
}
