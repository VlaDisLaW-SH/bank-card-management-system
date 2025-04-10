package com.card_management.technical.util;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class CardEncryptor {
    private final TextEncryptor encryptor;

    public CardEncryptor(String password, String salt) {
        this.encryptor = Encryptors.text(password, salt);
    }

    public String encryptCardNumber(String cardNumber) {
        return encryptor.encrypt(cardNumber);
    }

    public String decryptCardNumber(String encryptedCardNumber) {
        return encryptor.decrypt(encryptedCardNumber);
    }
}
