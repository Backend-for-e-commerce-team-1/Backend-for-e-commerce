package ru.practikum.masters.goodsservice.product.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "products"
        , indexes = {
        @Index(name = "idx_product_name", columnList = "product_name"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_brand", columnList = "brand_id"),
        @Index(name = "idx_product_code", columnList = "code", unique = true),
        @Index(name = "idx_product_price", columnList = "price")
})

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(name = "id", columnDefinition = "UUID")
    UUID id;

    @Column(name = "product_name")
    @EqualsAndHashCode.Include
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price")
    BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;

    @Column(name = "code")
    String code;

    @Column(name = "stock_quantity")
    Integer stockQuantity;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductReview> reviews = new ArrayList<>();

    public static Product create(String code, String name, String description, BigDecimal price, Category category, Brand brand, List<String> imageUrl) {

        Product product = new Product();
        product.code = code;
        product.name = name;
        product.description = description;
        product.price = price;
        product.category = category;
        product.brand = brand;
        product.imageUrls = imageUrl.stream().map(s -> {
            return ProductImage.createWithName(product, s, false);
        }).toList();
        return product;
    }

    public static Product create(String code, String name, String description, BigDecimal price, Category category, Brand brand, Integer stockQuantity, List<String> imageUrl) {

        var result = create(code, name, description, price, category, brand,  imageUrl);
        result.stockQuantity = stockQuantity;
        return result;
    }

}
