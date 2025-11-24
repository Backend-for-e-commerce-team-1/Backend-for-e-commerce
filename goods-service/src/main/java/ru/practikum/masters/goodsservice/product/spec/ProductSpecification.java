package ru.practikum.masters.goodsservice.product.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductSearchRequest;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductSpecification {
    public Specification<Product> buildFromFilter(ProductFilterRequest filter) {
        return Specification.allOf(
                withCategory(filter.getCategory()),
                withBrand(filter.getBrand()),
                withPriceBetween(filter.getPriceMin(), filter.getPriceMax())
        );
    }

    public Specification<Product> search(ProductSearchRequest request) {
        List<String> fields = request.getFields();
        String query = request.getQuery();

        if (fields == null || fields.isEmpty()) {
            throw new ValidationException("fields parameter is required and must be non-empty");
        }

        Set<String> allowed = java.util.Set.of("code", "brand", "name");
        Set<String> fieldSet;
        if (fields.size() == 1 && fields.get(0) != null && fields.get(0).contains(",")) {
            fieldSet = Arrays.stream(fields.get(0).split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        } else {
            fieldSet = new HashSet<>(fields);
        }
        fieldSet = fieldSet.stream().map(String::toLowerCase).collect(Collectors.toSet());
        if (!allowed.containsAll(fieldSet)) {
            throw new ValidationException("fields must be subset of: code, brand, name");
        }

        Specification<Product> base = searchByFields(query, fieldSet);

        boolean hasExplicitSort = request.getSort() != null && !request.getSort().isBlank();
        if (hasExplicitSort) {
            return base;
        }

        final String ql = query.trim().toLowerCase();
        final Set<String> fieldsFinal = fieldSet;
        return (root, q, cb) -> {
            Predicate predicate = base.toPredicate(root, q, cb);
            if (predicate == null) {
                return null;
            }

            Expression<Integer> nameScore = cb.literal(0);
            if (fieldsFinal.contains("name")) {
                nameScore = cb.<Integer>selectCase()
                        .when(cb.equal(cb.lower(root.get("name")), ql), 3)
                        .when(cb.like(cb.lower(root.get("name")), ql + "%"), 2)
                        .when(cb.like(cb.lower(root.get("name")), "%" + ql + "%"), 1)
                        .otherwise(0);
            }

            Expression<Integer> brandScore = cb.literal(0);
            if (fieldsFinal.contains("brand")) {
                brandScore = cb.<Integer>selectCase()
                        .when(cb.equal(cb.lower(root.join("brand").get("name")), ql), 3)
                        .when(cb.like(cb.lower(root.join("brand").get("name")), ql + "%"), 2)
                        .when(cb.like(cb.lower(root.join("brand").get("name")), "%" + ql + "%"), 1)
                        .otherwise(0);
            }

            Expression<Integer> codeScore = cb.literal(0);
            if (fieldsFinal.contains("code")) {
                codeScore = cb.<Integer>selectCase()
                        .when(cb.equal(cb.lower(root.get("code")), ql), 3)
                        .when(cb.like(cb.lower(root.get("code")), ql + "%"), 2)
                        .when(cb.like(cb.lower(root.get("code")), "%" + ql + "%"), 1)
                        .otherwise(0);
            }

            Expression<Integer> relevance = cb.sum(cb.sum(nameScore, brandScore), codeScore);
            q.orderBy(cb.desc(relevance), cb.asc(root.get("name")));
            return predicate;
        };
    }

    public Specification<Product> searchByFields(String query, Set<String> fields) {
        if (query == null || query.isBlank() || fields == null || fields.isEmpty()) {
            return (root, q, cb) -> null;
        }
        String like = "%" + query.trim().toLowerCase() + "%";
        List<Specification<Product>> specs = new ArrayList<>();
        if (fields.contains("name")) {
            specs.add((root, q, cb) -> cb.like(cb.lower(root.get("name")), like));
        }
        if (fields.contains("brand")) {
            specs.add((root, q, cb) -> cb.like(cb.lower(root.join("brand").get("name")), like));
        }
        if (fields.contains("code")) {
            specs.add((root, q, cb) -> cb.like(cb.lower(root.get("code")), like));
        }
        if (specs.isEmpty()) {
            return (root, q, cb) -> null;
        }
        return Specification.anyOf(specs);
    }

    private Specification<Product> withCategory(String categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return null;
            }
            try {
                UUID uuid = UUID.fromString(categoryId);
                return cb.equal(root.get("category").get("id"), uuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    private Specification<Product> withBrand(String brandId) {
        return (root, query, cb) -> {
            if (brandId == null) {
                return null;
            }
            try {
                UUID uuid = UUID.fromString(brandId);
                return cb.equal(root.get("brand").get("id"), uuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    private Specification<Product> withPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min != null && max != null) {
                return cb.between(root.get("price"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
        };
    }
}
