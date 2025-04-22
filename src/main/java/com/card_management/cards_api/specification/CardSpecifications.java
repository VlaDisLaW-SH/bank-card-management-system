package com.card_management.cards_api.specification;

import com.card_management.cards_api.dto.CardFilterDto;
import com.card_management.cards_api.model.Card;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
                LocalDateTime startOfDay = filter.getExactCreationDate().atStartOfDay();
                LocalDateTime endOfDay = filter.getExactCreationDate().atTime(LocalTime.MAX);

                predicates.add(criteriaBuilder.between(
                        root.get("createdAt"),
                        startOfDay,
                        endOfDay
                ));
            }

            if (filter.getCreatedAfter() != null) {
                LocalDateTime startOfDay = filter.getCreatedAfter().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        startOfDay
                ));
            }

            if (filter.getCreatedBefore() != null) {
                LocalDateTime endOfDay = filter.getCreatedBefore().atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        endOfDay
                ));
            }

            if (filter.getExactUpdateDate() != null) {
                LocalDateTime startOfDay = filter.getExactUpdateDate().atStartOfDay();
                LocalDateTime endOfDay = filter.getExactUpdateDate().atTime(LocalTime.MAX);

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
