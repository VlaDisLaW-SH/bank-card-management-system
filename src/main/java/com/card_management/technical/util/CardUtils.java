package com.card_management.technical.util;

import org.mapstruct.Named;

public class CardUtils {
    @Named("maskCardNumber")
    public static String maskCardNumber(String cardNumber) {
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
