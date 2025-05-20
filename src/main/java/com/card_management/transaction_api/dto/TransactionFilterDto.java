package com.card_management.transaction_api.dto;

import com.card_management.technical.validation.ValidFormatDate;
import com.card_management.technical.enumeration.SortDirection;
import com.card_management.transaction_api.enumeration.TransactionSortFields;
import com.card_management.transaction_api.enumeration.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "DTO для фильтрации транзакций")
public class TransactionFilterDto {

    @Schema(description = "Страница", example = "1", minimum = "1")
    @Positive(message = "Введите положительное значение")
    private Integer page;

    @Schema(description = "Кол-во элементов", example = "10", minimum = "1")
    @Positive(message = "Введите положительное значение")
    private Integer size;

    @Schema(
            description = "Значение сортировки",
            example = "createdAt",
            implementation = TransactionSortFields.class
    )
    private String sortBy;

    @Schema(
            description = "Направление сортировки",
            example = "DESC",
            implementation = SortDirection.class
    )
    private String sortDirection;

    @Schema(
            description = "Последние 4 цифры номера карты, используемой в качестве источника транзакции",
            example = "1234",
            minLength = 4,
            maxLength = 4
    )
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String sourceCardLastFour;

    @Schema(
            description = "Последние 4 цифры номера карты, используемой в качестве получателя средств или цели операции",
            example = "5678",
            minLength = 4,
            maxLength = 4
    )
    @Size(min = 4, max = 4, message = "Введите 4 последние цифры номера карты")
    private String destinationCardLastFour;

    @Schema(
            description = "Тип транзакции",
            example = "TRANSFER",
            implementation = TransactionType.class
    )
    private String transactionType;

    @Schema(
            description = "Сумма транзакции",
            example = "1000",
            minimum = "1"
    )
    @Positive(message = "Введите положительное значение")
    private Integer amount;

    @Schema(
            description = "Нижняя граница сумм транзакций (включая значение)",
            example = "500",
            minimum = "1"
    )
    @Positive(message = "Введите положительное значение")
    private Integer minAmount;

    @Schema(
            description = "Верхняя граница сумм транзакций (включая значение)",
            example = "10000",
            minimum = "1"
    )
    @Positive(message = "Введите положительное значение")
    private Integer maxAmount;

    @Schema(
            description = "Дата создания транзакции (точное значение)",
            example = "2025-05-15",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String exactDate;

    @Schema(
            description = "Дата транзакций созданных после указанного значения (включительно)",
            example = "2025-01-01",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String createdAfter;

    @Schema(
            description = "Дата транзакций созданных до указанного значения (включительно)",
            example = "2024-12-31",
            type = "string",
            format = "date"
    )
    @ValidFormatDate
    private String createdBefore;
}
