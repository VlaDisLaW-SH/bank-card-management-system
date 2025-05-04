package com.card_management.technical.util.factory;

import com.card_management.technical.util.CardEncryptor;
import org.springframework.stereotype.Component;

@Component
public class CardEncryptorFactoryImpl implements CardEncryptorFactory {
    @Override
    public CardEncryptor create(String password, String salt) {
        return new CardEncryptor(password, salt);
    }
}
