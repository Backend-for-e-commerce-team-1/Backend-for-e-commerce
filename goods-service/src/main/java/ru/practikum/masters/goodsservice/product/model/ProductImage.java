package ru.practikum.masters.goodsservice.product.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_image_product", columnList = "product_id"),
        @Index(name = "idx_image_primary", columnList = "is_primary")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "is_primary")
    Boolean isPrimary = false;

    public static ProductImage createWithName(Product product, String imageUrl, Boolean isPrimary) {
        ProductImage productImage = new ProductImage();
        productImage.product = product;
        productImage.imageUrl = imageUrl;
        productImage.isPrimary = isPrimary;
        return productImage;
    }
}
