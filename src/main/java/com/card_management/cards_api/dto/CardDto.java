package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.users_api.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class CardDto {

    private Long id;

    /**
     * Номер карты
     */
    private String cardNumber;

    /**
     * Владелец карты
     */
    private UserDto owner;

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
    private Long balance;

    /**
     * Список транзакций
     */
    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    //private List<Transaction> comments = new ArrayList<>();

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
