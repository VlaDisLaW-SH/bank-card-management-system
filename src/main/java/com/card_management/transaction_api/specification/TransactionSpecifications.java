package com.card_management.transaction_api.specification;

import com.card_management.transaction_api.dto.TransactionFilterDto;
import com.card_management.transaction_api.model.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> withFilter(TransactionFilterDto filter, Long userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                ));
            }

            if (filter.getSourceCardLastFour() != null) {
                String pattern = "%" + filter.getSourceCardLastFour();
                predicates.add(criteriaBuilder.like(
                        root.get("source").get("maskNumber"),
                        pattern
                ));
            }

            if (filter.getDestinationCardLastFour() != null) {
                String pattern = "%" + filter.getDestinationCardLastFour();
                predicates.add(criteriaBuilder.like(
                        root.get("destination").get("maskNumber"),
                        pattern
                ));
            }

            if (filter.getTransactionType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("transactionType"),
                        filter.getTransactionType()
                ));
            }

            if (filter.getAmount() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("amount"),
                        filter.getAmount()
                ));
            }

            if (filter.getMinAmount() != null) {
                predicates.add(criteriaBuilder.ge(
                        root.get("amount"),
                        filter.getMinAmount()
                ));
            }

            if (filter.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.le(
                        root.get("amount"),
                        filter.getMaxAmount()
                ));
            }

            if (filter.getExactDate() != null) {
                LocalDateTime startOfDay = filter.getExactDate().atStartOfDay();
                LocalDateTime endOfDay = filter.getExactDate().atTime(LocalTime.MAX);

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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
