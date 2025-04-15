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

    /**
     * Находит карту по полному номеру, сравнивая с маскированными номерами в БД.
     * Сравнивает первые 4 и последние 4 цифры полного номера с маскированным номером
     * в формате "4444****5555".
     *
     * @param fullCardNumber полный 16-значный номер карты для поиска
     * @return найденная карта или null, если не найдена
     * @see #findByMaskNumberLike(String)
     */
    default Card findByMaskedNumber(String fullCardNumber) {
        String first4 = fullCardNumber.substring(0, 4);
        String last4 = fullCardNumber.substring(12);
        String maskPattern = first4 + "%" + last4;
        return findByMaskNumberLike(maskPattern);
    }

    /**
     * Находит карту по частичному совпадению маскированного номера.
     * Используется внутренне методом findByMaskedNumber.
     *
     * @param maskPattern шаблон для поиска (например, "4444%5555")
     * @return найденная карта или null, если не найдена
     */
    Card findByMaskNumberLike(String maskPattern);
}
