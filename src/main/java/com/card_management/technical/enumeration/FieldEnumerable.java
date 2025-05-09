package com.card_management.technical.enumeration;

import java.util.Arrays;

/**
 * Интерфейс для перечислений (enum), которые поддерживают поиск по строковым значениям полей.
 * Позволяет проверять наличие поля в enum без явного перебора значений.
 */
public interface FieldEnumerable {
    /**
     * Возвращает строковое значение поля enum.
     *
     * @return строковое представление поля enum (не должно быть null)
     */
    String getField();

    /**
     * Проверяет, существует ли в указанном enum поле с заданным именем (без учета регистра).
     *
     * @param <E> тип enum, реализующего FieldEnumerable
     * @param enumClass класс enum для проверки (не null)
     * @param fieldName имя поля для поиска (регистронезависимое)
     * @return true если поле существует, false если нет
     * @throws NullPointerException если enumClass или fieldName равен null
     *
     * @example
     * <pre>{@code
     * // Проверка существования поля
     * boolean exists = FieldEnumerable.containsField(UserSortFields.class, "accessType");
     * }</pre>
     */
    static <E extends Enum<E> & FieldEnumerable> boolean containsField(Class<E> enumClass, String fieldName) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(FieldEnumerable::getField)
                .anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }
}
