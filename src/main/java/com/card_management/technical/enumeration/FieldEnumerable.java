package com.card_management.technical.enumeration;

import java.util.Arrays;

public interface FieldEnumerable {
    String getField();

    static <E extends Enum<E> & FieldEnumerable> boolean containsField(Class<E> enumClass, String fieldName) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(FieldEnumerable::getField)
                .anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }
}
