package com.card_management.cards_api.model;

import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.users_api.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * Зашифрованный номер карты
     */
    @Column(name = "number")
    private String encryptedCardNumber;

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
    private Long balance;

    /**
     * Список транзакций
     */
    //@OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Transaction> transactions = new ArrayList<>();

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
