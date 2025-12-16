package ru.practikum.masters.goodsservice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.masters.goodsservice.product.model.ProductImage;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(UUID productId);

    List<ProductImage> findByIsPrimaryTrue();
}
