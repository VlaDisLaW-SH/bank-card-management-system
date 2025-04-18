package com.card_management.cards_api.model;

import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.transaction_api.model.Transaction;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Банковская карта
 */
@Getter
@Setter
@Entity
@Table(name = "cards")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    /**
     * UUID карты
     */
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Зашифрованный номер карты
     */
    @Column(nullable = false, name = "encrypted_card_number")
    private String encryptedCardNumber;

    /**
     * Замаскированный номер карты в формате 0000****0000
     */
    @Column(name = "mask_number")
    private String maskNumber;

    /**
     * Владелец карты
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Месяц окончания действия карты
     */
    @NotNull
    @Column(name = "validity_period_month")
    private Integer validityPeriodMonth;

    /**
     * Год окончания действия карты
     */
    @NotNull
    @Column(name = "validity_period_year")
    private Integer validityPeriodYear;

    /**
     * Статус карты
     */
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    /**
     * Баланс карты
     */
    @NotNull
    private Integer balance;

    /**
     * Список исходящих транзакций
     */
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    /**
     * Список входящих транзакций
     */
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> incomingTransactions = new ArrayList<>();

    /**
     * Соль для расшифровки номера карты
     */
    private String saltNumberCard;

    /**
     * Дата и время создания карты
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время обновления карты
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
