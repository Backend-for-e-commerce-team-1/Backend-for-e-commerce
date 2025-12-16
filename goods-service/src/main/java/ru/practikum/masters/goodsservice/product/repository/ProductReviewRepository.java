package ru.practikum.masters.goodsservice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.masters.goodsservice.product.model.ProductReview;

import java.util.List;
import java.util.UUID;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductId(UUID productId);

    List<ProductReview> findByUserId(UUID userId);

    Double findAverageRatingByProductId(UUID productId);
}
