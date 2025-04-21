package com.card_management.transaction_api.repository;

import com.card_management.transaction_api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
