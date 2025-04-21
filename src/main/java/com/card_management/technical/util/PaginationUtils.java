package com.card_management.technical.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public class PaginationUtils {

    public static  <T> Pageable createPageable(T filterDto,
                                               Function<T, String> getSortBy,
                                               Function<T, String> getSortDirection,
                                               Function<T, Integer> getPage,
                                               Function<T, Integer> getSize,
                                               String sortDefault,
                                               String sortDirectionDefault
    ) {
        int page = getPage.apply(filterDto) != null ? getPage.apply(filterDto) : 1;
        int size = getSize.apply(filterDto) != null ? getSize.apply(filterDto) : 10;
        String sortBy = getSortBy.apply(filterDto) != null && !getSortBy.apply(filterDto).isEmpty()
                ? getSortBy.apply(filterDto) : sortDefault;
        String sortDirection = getSortDirection.apply(filterDto) != null && !getSortDirection.apply(filterDto).isEmpty()
                ? getSortDirection.apply(filterDto) : sortDirectionDefault.toUpperCase();

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page - 1, size, sort);
    }
}
