package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class CardDto {

    private Long id;

    /**
     * UUID карты
     */
    private UUID uuid;

    /**
     * Замаскированный номер карты в формате 0000****0000
     */
    private String maskNumber;

    /**
     * UUID владельца карты
     */
    private UUID ownerUuid;

    /**
     * Месяц окончания действия карты
     */
    private Integer validityPeriodMonth;

    /**
     * Год окончания действия карты
     */
    private Integer validityPeriodYear;

    /**
     * Статус карты
     */
    private CardStatus status;

    /**
     * Баланс карты
     */
    private Integer balance;

    /**
     * Дата и время создания карты
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Дата и время обновления карты
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
