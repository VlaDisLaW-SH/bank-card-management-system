package com.card_management.users_api.model;

import com.card_management.cards_api.model.Card;
import com.card_management.limits_api.model.Limit;
import com.card_management.transaction_api.model.Transaction;
import com.card_management.users_api.enumeration.UserAccessType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Позьзователь
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    /**
     * UUID пользователя
     */
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Фамилия
     */
    @NotBlank
    @Column(name = "surname")
    private String surname;

    /**
     * Имя
     */
    @NotBlank
    @Column(name = "name")
    private String name;

    /**
     * Отчество
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Адрес электронной почты
     */
    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    /**
     * Пароль
     */
    @Column(name = "password",nullable = false, length = 255)
    private String password;

    /**
     * Роль пользователя в системе
     */
    @NotNull
    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    private UserAccessType accessType;

    /**
     * Список карт пользователя
     */
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Card> listCards = new ArrayList<>();

    /**
     * Список лимитов
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Limit> limits = new ArrayList<>();

    /**
     * Список транзакций пользователя
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * Дата и время создания пользователя
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
