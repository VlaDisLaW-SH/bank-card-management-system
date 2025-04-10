package com.card_management.limits_api.dto;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.transaction_api.enumeration.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class LimitDto {

    private Long id;

    /**
     * UUID лимита
     */
    private UUID uuid;

    /**
     * UUID пользователя для которого устанавливается лимит
     */
    private UUID userUuid;

    /**
     * Тип лимита
     */
    private LimitType limitType;

    /**
     * Тип транзакции
     */
    private TransactionType transactionType;

    /**
     * Сумма лимита
     */
    private Integer limitAmount;

    /**
     * Дата и время создания лимита
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Дата и время обновления лимита
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
