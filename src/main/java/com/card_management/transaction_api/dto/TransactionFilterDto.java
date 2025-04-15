package com.card_management.transaction_api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class TransactionFilterDto {
    /**
     * Страница
     */
    @Positive(message = "Введите положительное значение")
    private Integer page;

    /**
     * Кол-во элементов
     */
    @Positive(message = "Введите положительное значение")
    private Integer size;

    /**
     * Значение сортировки
     */
    private String sortBy;

    /**
     * Направление сортировки
     */
    private String sortDirection;

    /**
     * UUID инициатора транзакции
     */
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Неверный формат UUID"
    )
    private String userUuid;

    /**
     * Последние 4 цифры номера карты, используемой в качестве источника транзакции
     */
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String sourceCardLastFour;

    /**
     * Последние 4 цифры номера карты, используемой в качестве получателя средств или цели операции
     */
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String destinationCardLastFour;

    /**
     * Тип транзакции
     */
    private String transactionType;

    /**
     * Сумма транзакции
     */
    @Positive(message = "Введите положительное значение")
    private Integer amount;

    /**
     * Нижняя граница сумм транзакций (включая значение)
     */
    @Positive(message = "Введите положительное значение")
    private Integer minAmount;

    /**
     * Верхняя граница сумм транзакций (включая значение)
     */
    @Positive(message = "Введите положительное значение")
    private Integer maxAmount;

    /**
     * Дата создания транзакции (точное значение)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate exactDate;

    /**
     * Дата транзакций созданных после указанного значения (включительно)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAfter;

    /**
     * Дата транзакций созданных до указанного значения (включительно)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdBefore;
}
