package com.card_management.factory.integration;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.service.CardService;
import com.card_management.users_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardTestFactory {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;

    public void createCard(String cardNumber, Long userId, String cardStatus, Integer balance) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("User with ID " + userId + " does not exist");
        }
        var dto = new CardCreateDto();
        dto.setCardNumber(cardNumber);
        dto.setOwnerId(userId);
        dto.setValidityPeriodMonth(10);
        dto.setValidityPeriodYear(28);
        dto.setStatus(cardStatus);
        dto.setBalance(balance);
        cardService.create(dto);
    }
}
