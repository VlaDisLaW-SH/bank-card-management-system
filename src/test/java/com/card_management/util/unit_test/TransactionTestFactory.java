package com.card_management.util.unit_test;

import com.card_management.cards_api.model.Card;
import com.card_management.transaction_api.dto.TransactionDto;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.transaction_api.model.Transaction;
import com.card_management.users_api.model.User;

public class TransactionTestFactory {

    public static Transaction createTransaction(
            Long id,
            User user,
            Card source,
            Card destination,
            TransactionType transactionType,
            Integer amount

    ) {
        var transaction = new Transaction();
        transaction.setId(id);
        transaction.setUser(user);
        transaction.setSource(source);
        transaction.setDestination(destination);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        return transaction;
    }

    public static TransactionDto createTransactionDto(Transaction transaction) {
        var dto = new TransactionDto();
        dto.setMaskedSource(transaction.getSource().getMaskNumber());
        dto.setMaskedDestination(transaction.getDestination().getMaskNumber());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        return dto;
    }
}
