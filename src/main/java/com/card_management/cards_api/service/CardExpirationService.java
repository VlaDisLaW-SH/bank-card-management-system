package com.card_management.cards_api.service;

import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.model.Card;
import com.card_management.cards_api.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardExpirationService {
    private final CardRepository cardRepository;

    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void checkAndUpdateExpiredCards() {
        List<Card> activeCards = cardRepository.findByStatus(CardStatus.ACTIVE);

        activeCards.stream()
                .filter(card -> isExpired(card))
                .forEach(card -> {
                    card.setStatus(CardStatus.EXPIRED);
                    cardRepository.save(card);
                });
    }

    private boolean isExpired(Card card) {
        YearMonth currentYearMonth = YearMonth.now();
        int fullYear = 2000 + card.getValidityPeriodYear();
        YearMonth cardYearMonth = YearMonth.of(fullYear, card.getValidityPeriodMonth());
        return cardYearMonth.isBefore(currentYearMonth);
    }
}
