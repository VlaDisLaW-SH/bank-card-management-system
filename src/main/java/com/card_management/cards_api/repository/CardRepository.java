package com.card_management.cards_api.repository;

import com.card_management.cards_api.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Card> findByOwnerId(Long userId, Pageable pageable);
}
