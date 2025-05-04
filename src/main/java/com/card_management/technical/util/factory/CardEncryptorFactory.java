package com.card_management.technical.util.factory;

import com.card_management.technical.util.CardEncryptor;

public interface CardEncryptorFactory {
    CardEncryptor create(String password, String salt);
}
