package com.card_management.transaction_api.repository;

import com.card_management.transaction_api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Интерфейс-репозиторий для транзакций
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {
    /**
     * Поиск всех транзакций принадлежащих пользователю
     * @param userId ID пользователя
     * @param pageable объект, содержащий информацию о пагинации и сортировке.
     * @return список транзакций в виде объекта Page
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Находит все транзакции для указанного пользователя и карты (по последним 4 цифрам номера)
     * @param userUuid UUID пользователя
     * @param lastFourDigits последние 4 цифры номера карты (источника или получателя)
     * @param pageable объект, содержащий информацию о пагинации и сортировке.
     * @return список транзакций в виде объекта Page
     */
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.user.uuid = :userUuid " +
            "AND (t.source.maskNumber LIKE %:lastFourDigits OR " +
            "    (t.destination IS NOT NULL AND t.destination.maskNumber LIKE %:lastFourDigits) OR " +
            "    (t.destination IS NULL AND t.source.maskNumber LIKE %:lastFourDigits))")
    Page<Transaction> findByUserUuidAndCardLastFourDigits(
            @Param("userUuid") UUID userUuid,
            @Param("lastFourDigits") String lastFourDigits,
            Pageable pageable);
}
