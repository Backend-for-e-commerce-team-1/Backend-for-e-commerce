package ru.practikum.masters.goodsservice.product.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.product.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@Component
public class ProductSpecification {
    public Specification<Product> buildFromFilter(ProductFilterRequest filter) {
        return Specification.allOf(
                withCategory(filter.getCategory()),
                withBrand(filter.getBrand()),
                withPriceBetween(filter.getPriceMin(), filter.getPriceMax())
        );
    }

    public Specification<Product> searchByFields(String query, Set<String> fields) {
        if (query == null || query.isBlank() || fields == null || fields.isEmpty()) {
            return (root, q, cb) -> null;
        }
        String like = "%" + query.trim().toLowerCase() + "%";
        java.util.List<Specification<Product>> specs = new ArrayList<>();
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

    private Specification<Product> withPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }

            BigDecimal minBd = min != null ? BigDecimal.valueOf(min) : null;
            BigDecimal maxBd = max != null ? BigDecimal.valueOf(max) : null;

            if (minBd != null && maxBd != null) {
                return cb.between(root.get("price"), minBd, maxBd);
            } else if (minBd != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minBd);
            } else {
                return cb.lessThanOrEqualTo(root.get("price"), maxBd);
            }
        };
    }
}
