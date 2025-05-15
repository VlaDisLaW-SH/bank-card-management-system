package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для создания новой банковской карты")
public class CardCreateDto {

    @Schema(
            description = "Номер банковской карты (16 цифр, соответствует стандарту ISO/IEC 7812)",
            example = "1234567812345678",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 16,
            maxLength = 16
    )
    @NotNull(message = "Номер карты обязателен")
    @NotBlank(message = "Номер карты не должен быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать ровно 16 цифр")
    @CreditCardNumber(message = "Недействительный номер карты")
    private String cardNumber;

    @Schema(
            description = "ID владельца карты",
            example = "1001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID владельца обязателен")
    private Long ownerId;

    @Schema(
            description = "Месяц окончания действия карты",
            example = "12",
            minimum = "1",
            maximum = "12",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите месяц окончания действия")
    @Min(value = 1, message = "Месяц должен быть не менее 1")
    @Max(value = 12, message = "Месяц должен быть не более 12")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodMonth;

    @Schema(
            description = "Год окончания действия карты",
            example = "27",
            minimum = "10",
            maximum = "50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите год окончания действия")
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodYear;

    @Schema(
            description = "Статус карты",
            example = "ACTIVE",
            implementation = CardStatus.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите статус")
    @NotBlank(message = "Статус не должен быть пустым")
    private String status;

    @Schema(
            description = "Баланс карты",
            example = "5000",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Введите баланс карты")
    @Positive(message = "Введите положительное значение")
    private Integer balance;
}
