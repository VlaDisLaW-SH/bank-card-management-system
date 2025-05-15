package com.card_management.factory.integration;

import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestFactory {

    private final TransactionService transactionService;

    @Autowired
    public TransactionTestFactory(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void createTransaction(
            String sourceCardNumber,
            String destinationCardNumber,
            String transactionType,
            Integer amount,
            Long userId
    ) {
        var dto = new TransactionCreateDto();
        dto.setSourceNumber(sourceCardNumber);
        dto.setDestinationNumber(destinationCardNumber);
        dto.setTransactionType(transactionType);
        dto.setAmount(amount);

        transactionService.create(dto, userId);
    }
}
