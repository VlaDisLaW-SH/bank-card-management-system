package com.card_management.cards_api.dto;

import com.card_management.technical.validation.ValidFormatDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CardFilterDto {
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
     * Последние 4 цифры номера карты
     */
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String maskNumberLastFour;

    /**
     * Месяц окончания действия карты
     */
    @Min(value = 1, message = "Месяц должен быть не менее 1")
    @Max(value = 12, message = "Месяц должен быть не более 12")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodMonth;

    /**
     * Год окончания действия карты
     */
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodYear;

    /**
     * Год окончания действия карты, который должен быть больше или равен указанному значению
     */
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer greaterThanValidityPeriodYear;

    /**
     * Год окончания действия карты, который должен быть меньше или равен указанному значению
     */
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer lesserThanValidityPeriodYear;

    /**
     * Статус карты
     */
    private String status;

    /**
     * Нижняя граница сумм баланса карт (включая значение)
     */
    @Positive(message = "Введите положительное значение")
    private Integer greaterThanBalance;

    /**
     * Верхняя граница сумм баланса карт (включая значение)
     */
    @Positive(message = "Введите положительное значение")
    private Integer lesserThanBalance;

    /**
     * Дата создания карты (точное значение)
     */
    @ValidFormatDate
    private String exactCreationDate;

    /**
     * Дата карт созданных после указанного значения (включительно)
     */
    @ValidFormatDate
    private String createdAfter;

    /**
     * Дата карт созданных до указанного значения (включительно)
     */
    @ValidFormatDate
    private String createdBefore;

    /**
     * Дата обновления карты (точное значение)
     */
    @ValidFormatDate
    private String exactUpdateDate;
}
