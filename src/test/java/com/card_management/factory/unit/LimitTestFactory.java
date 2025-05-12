package com.card_management.factory.unit;

import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.model.Limit;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.card_management.users_api.model.User;

public class LimitTestFactory {
    public static Limit createLimit(
            Long id,
            User user,
            LimitType limitType,
            TransactionType transactionType,
            Integer amount
    ) {
        Limit limit = new Limit();
        limit.setId(id);
        limit.setUser(user);
        limit.setLimitType(limitType);
        limit.setTransactionType(transactionType);
        limit.setLimitAmount(amount);
        return limit;
    }

    public static LimitDto createLimitDto(Limit limit) {
        LimitDto dto = new LimitDto();
        dto.setId(limit.getId());
        dto.setLimitType(limit.getLimitType());
        dto.setTransactionType(limit.getTransactionType());
        dto.setLimitAmount(limit.getLimitAmount());
        return dto;
    }
}
