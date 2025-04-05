package com.card_management.users_api.model;

import com.card_management.cards_api.model.Card;
import com.card_management.users_api.enumeration.UserAccessType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "patronymic")
    private String patronymic;

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

    //todo Limits

    /**
     * Дата и время создания пользователя
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
