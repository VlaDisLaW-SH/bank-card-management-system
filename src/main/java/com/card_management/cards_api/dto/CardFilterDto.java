package com.card_management.cards_api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
     * UUID владельца карты
     */
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Неверный формат UUID"
    )
    private String ownerUuid;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate exactCreationDate;

    /**
     * Дата карт созданных после указанного значения (включительно)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAfter;

    /**
     * Дата карт созданных до указанного значения (включительно)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdBefore;

    /**
     * Дата обновления карты (точное значение)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate exactUpdateDate;
}
