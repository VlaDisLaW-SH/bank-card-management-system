package com.card_management.cards_api.repository;

import com.card_management.cards_api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс-репозиторий для карт
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}
