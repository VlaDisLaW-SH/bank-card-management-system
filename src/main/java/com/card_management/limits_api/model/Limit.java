package com.card_management.limits_api.model;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Общие лимиты на операции снятие и переводы
 */
@Getter
@Setter
@Entity
@Table(name = "limits")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    /**
     * UUID лимита
     */
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Пользователь для которого устанавливаются лимиты
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    /**
     * Тип лимита
     */
    @NotNull
    @Column(name = "limit_type")
    @Enumerated(EnumType.STRING)
    private LimitType limitType;

    /**
     * Тип транзакции
     */
    @NotNull
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    /**
     * Сумма лимита
     */
    @NotNull
    @Column(name = "limit_amount")
    private Integer limitAmount;

    /**
     * Сумма текущих расходов по картам
     */
    @Column(name = "current_expenses_amount")
    private Integer currentExpensesAmount = 0;

    /**
     * Дата последней транзакции
     */
    @Column(name = "date_last_transaction")
    private LocalDate dateLastTransaction = null;

    /**
     * Дата и время создания лимита
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время обновления лимита
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
