package ru.practikum.masters.goodsservice.product.configuration.pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PageableFactory {

    private final PaginationProperties paginationProperties;

    public Pageable createFromFilter(ProductFilterRequest filter) {
        final String tag = "PageableFactory.createFromFilter";
        log.debug("{}: Enter with params: filter={}", tag, filter);
        try {
            validatePaginationParams(filter);

            int offset = filter.getOffset() != null ? filter.getOffset() : 0;
            int limit = filter.getLimit() != null ?
                    Math.min(filter.getLimit(), paginationProperties.getMaxPageSize()) :
                    paginationProperties.getDefaultPageSize();
            log.debug("{}: Calculated offset={}, limit={}", tag, offset, limit);

            int pageNumber = (filter.getPage() != null && filter.getPage() > 0)
                    ? filter.getPage() - 1
                    : calculatePageNumber(offset, limit);
            log.debug("{}: Calculated pageNumber={}", tag, pageNumber);

            Sort sort = createSort(filter.getSort());
            log.debug("{}: Created sort={}", tag, sort);

            Pageable pageable = sort != null
                    ? PageRequest.of(pageNumber, limit, sort)
                    : PageRequest.of(pageNumber, limit);
            log.info("{}: Pageable created successfully", tag);
            log.debug("{}: Exit with result: {}", tag, pageable);
            return pageable;
        } catch (RuntimeException e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    private int calculatePageNumber(int offset, int limit) {
        if (limit <= 0) {
            log.error("{}: Validation failed - limit <= 0 (limit={})", "PageableFactory.calculatePageNumber", limit);
            throw new ValidationException("Limit must be greater than 0");
        }
        return offset / limit;
    }

    private Sort createSort(String sortParam) {
        if (!StringUtils.hasText(sortParam)) {
            return null;
        }
        Sort aliasSort = parseAliasSort(sortParam);
        if (aliasSort != null) {
            return aliasSort;
        }

        String[] sortExpressions = sortParam.split(";");
        List<Sort.Order> orders = new ArrayList<>();

        for (String expression : sortExpressions) {
            Sort.Order order = parseSortExpression(expression.trim());
            if (order != null) {
                orders.add(order);
            }
        }
        log.debug("{}: Parsed sort orders count={}", "PageableFactory.createSort", orders.size());
        return orders.isEmpty() ? null : Sort.by(orders);
    }

    private Sort parseAliasSort(String sortParam) {
        String p = sortParam.trim().toLowerCase();
        switch (p) {
            case "price_asc":
                return Sort.by(Sort.Order.asc("price"));
            case "price_desc":
                return Sort.by(Sort.Order.desc("price"));
            case "name_asc":
                return Sort.by(Sort.Order.asc("name"));
            case "name_desc":
                return Sort.by(Sort.Order.desc("name"));
            default:
                return null;
        }
    }

    private Sort.Order parseSortExpression(String expression) {
        if (!StringUtils.hasText(expression)) {
            return null;
        }

        String[] parts = expression.split(",");
        if (parts.length == 1) {
            return new Sort.Order(Sort.Direction.ASC, parts[0].trim());
        } else if (parts.length == 2) {
            Sort.Direction direction = parseDirection(parts[1].trim());
            return new Sort.Order(direction, parts[0].trim());
        }

        log.error("{}: Validation failed - invalid sort expression: {}", "PageableFactory.parseSortExpression", expression);
        throw new ValidationException("Invalid sort expression: " + expression);
    }

    private Sort.Direction parseDirection(String direction) {
        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            log.error("{}: Validation failed - invalid sort direction: {}", "PageableFactory.parseDirection", direction);
            throw new ValidationException("Invalid sort direction: " + direction + ". Use ASC or DESC");
        }
    }

    private void validatePaginationParams(ProductFilterRequest filter) {
        if (filter.getOffset() != null && filter.getOffset() < 0) {
            log.error("{}: Validation failed - offset is negative: {}", "PageableFactory.validatePaginationParams", filter.getOffset());
            throw new ValidationException("Offset must be non-negative");
        }
        if (filter.getLimit() != null && filter.getLimit() <= 0) {
            log.error("{}: Validation failed - limit <= 0: {}", "PageableFactory.validatePaginationParams", filter.getLimit());
            throw new ValidationException("Limit must be greater than 0");
        }
}
}
