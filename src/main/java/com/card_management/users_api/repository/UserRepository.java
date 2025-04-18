package com.card_management.users_api.repository;

import com.card_management.users_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс-репозиторий для пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Проверка на наличие адреса электронной почты в репозитории
     * @param email адрес электронной почты
     * @return булево значение
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователя по адресу электронной почты.
     * @param email адрес электронной почты
     * @return {@link Optional<User>} объект, содержащий найденного пользователя, если он существует
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователя по UUID
     * @param uuid UUID пользователя
     * @return пользователь
     */
    User findByUuid(UUID uuid);

    /**
     * Поиск пользователя по UUID
     * @param uuid UUID пользователя
     * @return булево значение
     */
    boolean existsByUuid(UUID uuid);
}
