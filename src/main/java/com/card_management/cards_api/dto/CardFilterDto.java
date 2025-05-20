package com.card_management.cards_api.dto;

import com.card_management.cards_api.enumeration.CardSortFields;
import com.card_management.cards_api.enumeration.CardStatus;
import com.card_management.technical.validation.ValidFormatDate;
import com.card_management.technical.enumeration.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO для фильтрации списка карт")
public class CardFilterDto {

    @Schema(description = "Номер страницы", example = "1", minimum = "1")
    @Positive(message = "Введите положительное значение")
    private Integer page;

    @Schema(description = "Количество элементов на странице", example = "10", minimum = "1")
    @Positive(message = "Введите положительное значение")
    private Integer size;

    @Schema(description = "Поле для сортировки",
            example = "id",
            implementation = CardSortFields.class
    )
    private String sortBy;

    @Schema(description = "Направление сортировки",
            example = "ASC",
            implementation = SortDirection.class
    )
    private String sortDirection;

    @Schema(description = "Последние 4 цифры маскированного номера карты",
            example = "1234",
            minLength = 4,
            maxLength = 4
    )
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String maskNumberLastFour;

    @Schema(description = "Месяц окончания действия карты", example = "12", minimum = "1", maximum = "12")
    @Min(value = 1, message = "Месяц должен быть не менее 1")
    @Max(value = 12, message = "Месяц должен быть не более 12")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodMonth;

    @Schema(description = "Год окончания действия карты", example = "27", minimum = "10", maximum = "50")
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer validityPeriodYear;

    @Schema(description = "Год окончания действия карты, который должен быть больше или равен указанному значению",
            example = "20",
            minimum = "10",
            maximum = "50"
    )
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer greaterThanValidityPeriodYear;

    @Schema(description = "Год окончания действия карты, который должен быть меньше или равен указанному значению",
            example = "30",
            minimum = "10",
            maximum = "50"
    )
    @Min(value = 10, message = "Год должен быть не менее 10")
    @Max(value = 50, message = "Год должен быть не более 50")
    @Positive(message = "Введите положительное значение")
    private Integer lesserThanValidityPeriodYear;

    @Schema(
            description = "Статус карты",
            example = "ACTIVE",
            implementation = CardStatus.class
    )
    private String status;

    @Schema(description = "Нижняя граница сумм баланса карт (включая значение)", example = "1000", minimum = "0")
    @Positive(message = "Введите положительное значение")
    private Integer greaterThanBalance;

    @Schema(description = "Верхняя граница сумм баланса карт (включая значение)", example = "10000", minimum = "0")
    @Positive(message = "Введите положительное значение")
    private Integer lesserThanBalance;

    @Schema(
            description = "Дата создания карты (точное значение, формат: yyyy-MM-dd)",
            example = "2025-01-01",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String exactCreationDate;

    @Schema(
            description = "Дата создания карты после указанного значения (включительно)",
            example = "2025-01-01",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String createdAfter;

    @Schema(
            description = "Дата создания карты до указанного значения (включительно)",
            example = "2024-12-31",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String createdBefore;

    @Schema(
            description = "Дата обновления карты (точное значение)",
            example = "2025-01-01",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String exactUpdateDate;
}
