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
     * Получение списка лимитов пользователя
     * @param userId ID пользователя
     * @return список лимитов
     */
    List<Limit> getLimitsByUserId(Long userId);
}
