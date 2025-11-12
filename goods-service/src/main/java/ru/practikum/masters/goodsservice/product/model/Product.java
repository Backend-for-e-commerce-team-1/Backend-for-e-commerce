package ru.practikum.masters.goodsservice.product.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(unique = true)
    private String code;
    
    @Column
    private String description;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Brand brand;

    @Column(columnDefinition = "jsonb")
    private List<String> imageUrls;

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
