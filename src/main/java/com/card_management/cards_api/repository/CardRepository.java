package com.card_management.cards_api.repository;

import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.cards_api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс-репозиторий для карт
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * Поиск всех карт принадлежащих пользователю
     * @param userId ID пользователя
     * @return список карт
     */
    List<Card> findByOwnerId(Long userId);

    /**
     * Поиск всех карт с определенным статусом
     * @param status статус карты
     * @return список карт
     */
    List<Card> findByStatus(CardStatus status);
}
