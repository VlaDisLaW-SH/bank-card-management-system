package com.card_management.limits_api.repository;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.model.Limit;
import com.card_management.transaction_api.enumeration.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс-репозиторий для лимитов
 */
@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    /**
     * Проверяет наличие активного лимита у пользователя по типу лимита и типу транзакции
     * @param userId ID пользователя
     * @param limitType тип лимита
     * @param transactionType тип транзакции
     * @return true - если лимит существует, false - если нет
     */
    boolean existsByUserIdAndLimitTypeAndTransactionType(
            Long userId,
            LimitType limitType,
            TransactionType transactionType);

    /**
     * Получает лимит пользователя по типу лимита и типу транзакции
     * @param userId ID пользователя
     * @param limitType тип лимита
     * @param transactionType тип транзакции
     * @return лимит
     */
    Limit getLimitByUserIdAndLimitTypeAndTransactionType(
            Long userId,
            LimitType limitType,
            TransactionType transactionType);

    /**
     * Получение списка лимитов пользователя
     * @param userId ID пользователя
     * @return список лимитов
     */
    List<Limit> getLimitsByUserId(Long userId);

    /**
     * Находит и возвращает список лимитов по указанному типу лимита.
     * @param limitType тип лимита, по которому необходимо выполнить поиск.
     *                  Не должен быть равен {@code null}.
     * @return список объектов лимитов, соответствующих заданному типу лимита.
     *         Если лимиты не найдены, возвращается пустой список.
     */
    List<Limit> findByLimitType(LimitType limitType);

    /**
     * Находит все сущности Limit, соответствующие указанному типу лимита и статусу ожидающего обновления.
     * @param limitType тип лимита для поиска (не может быть {@code null})
     * @param hasPendingUpdate флаг статуса ожидающего обновления:
     *        {@code true} - для лимитов с ожидающими обновлениями,
     *        {@code false} - для лимитов без ожидающих обновлений
     * @return список найденных сущностей Limit.
     *         Возвращает пустой список, если совпадений не найдено.
     */
    List<Limit> findByLimitTypeAndHasPendingUpdate(LimitType limitType, boolean hasPendingUpdate);
}
