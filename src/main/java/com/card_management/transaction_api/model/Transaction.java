package com.card_management.transaction_api.model;

import com.card_management.cards_api.model.Card;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Транзакция по банковской карте
 */
@Getter
@Setter
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    /**
     * UUID транзакции
     */
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Инициатор транзакции
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    /**
     * Источник транзакции
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "source_uuid")
    private Card source;

    /**
     * Карта, используемая в качестве получателя средств или цели операции
     */
    @ManyToOne
    @JoinColumn(name = "destination_uuid")
    private Card destination;

    /**
     * Тип транзакции
     */
    @NotNull
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    /**
     * Сумма транзакции
     */
    @NotNull
    @Column(name = "amount")
    private Integer amount;

    /**
     * Дата и время создания транзакции
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
