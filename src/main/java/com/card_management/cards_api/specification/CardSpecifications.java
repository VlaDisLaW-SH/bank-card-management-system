package com.card_management.cards_api.specification;

import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.model.Card;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CardSpecifications {

    public static Specification<Card> withFilter(CardFilterDto filter, Long ownerId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ownerId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("owner").get("id"),
                        ownerId
                ));
            }

            if (filter.getMaskNumberLastFour() != null) {
                predicates.add(criteriaBuilder.like(
                        root.get("maskNumber"),
                        "%" + filter.getMaskNumberLastFour()
                ));
            }

            if (filter.getValidityPeriodMonth() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("validityPeriodMonth"),
                        filter.getValidityPeriodMonth()
                ));
            }

            if (filter.getValidityPeriodYear() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("validityPeriodYear"),
                        filter.getValidityPeriodYear()
                ));
            }

            if (filter.getGreaterThanValidityPeriodYear() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("validityPeriodYear"),
                        filter.getGreaterThanValidityPeriodYear()
                ));
            }

            if (filter.getLesserThanValidityPeriodYear() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("validityPeriodYear"),
                        filter.getLesserThanValidityPeriodYear()
                ));
            }

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        filter.getStatus()
                ));
            }

            if (filter.getGreaterThanBalance() != null) {
                predicates.add(criteriaBuilder.ge(
                        root.get("balance"),
                        filter.getGreaterThanBalance()
                ));
            }

            if (filter.getLesserThanBalance() != null) {
                predicates.add(criteriaBuilder.le(
                        root.get("balance"),
                        filter.getLesserThanBalance()
                ));
            }

            if (filter.getExactCreationDate() != null) {
                LocalDate exactCreationDate = LocalDate.parse(
                        filter.getExactCreationDate(),
                        DateTimeFormatter.ISO_DATE
                );
                LocalDateTime startOfDay = exactCreationDate.atStartOfDay();
                LocalDateTime endOfDay = exactCreationDate.atTime(LocalTime.MAX);

                predicates.add(criteriaBuilder.between(
                        root.get("createdAt"),
                        startOfDay,
                        endOfDay
                ));
            }

            if (filter.getCreatedAfter() != null) {
                LocalDate createdAfter = LocalDate.parse(
                        filter.getCreatedAfter(),
                        DateTimeFormatter.ISO_DATE
                );
                LocalDateTime startOfDay = createdAfter.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        startOfDay
                ));
            }

            if (filter.getCreatedBefore() != null) {
                LocalDate createdBefore = LocalDate.parse(
                        filter.getCreatedBefore(),
                        DateTimeFormatter.ISO_DATE
                );
                LocalDateTime endOfDay = createdBefore.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        endOfDay
                ));
            }

            if (filter.getExactUpdateDate() != null) {
                LocalDate exactUpdateDate = LocalDate.parse(
                        filter.getExactUpdateDate(),
                        DateTimeFormatter.ISO_DATE
                );
                LocalDateTime startOfDay = exactUpdateDate.atStartOfDay();
                LocalDateTime endOfDay = exactUpdateDate.atTime(LocalTime.MAX);

                predicates.add(criteriaBuilder.between(
                        root.get("updatedAt"),
                        startOfDay,
                        endOfDay
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
