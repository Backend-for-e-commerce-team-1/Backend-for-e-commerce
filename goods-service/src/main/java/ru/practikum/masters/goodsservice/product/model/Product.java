package ru.practikum.masters.goodsservice.product.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_brand", columnList = "brand"),
        @Index(name = "idx_product_code", columnList = "code", unique = true),
        @Index(name = "idx_product_price", columnList = "price")
})

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "uuid")
    UUID id;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    String name;

    String description;

    @Column(nullable = false)
    BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @ManyToOne
    @JoinColumn(nullable = false)
    Brand brand;

    @Column(nullable = false, unique = true)
    String code;

    @Column(name = "stock_quantity", nullable = false)
    Integer stockQuantity;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    //    @Column(columnDefinition = "jsonb")
    private List<String> imageUrls;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductReview> reviews = new ArrayList<>();

    public static Product create(String code, String name, String description, BigDecimal price, Category category, Brand brand, List<String> imageUrls) {
        Product product = new Product();
        product.code = code;
        product.name = name;
        product.description = description;
        product.price = price;
        product.category = category;
        product.brand = brand;
        product.imageUrls = imageUrls;
        return product;
    }

}
