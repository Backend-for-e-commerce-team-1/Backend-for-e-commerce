package ru.practikum.masters.goodsservice.product.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practikum.masters.goodsservice.product.model.Product;

import java.util.ArrayList;
import java.util.Set;

@Component
public class ProductSearchSpecification {
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
}