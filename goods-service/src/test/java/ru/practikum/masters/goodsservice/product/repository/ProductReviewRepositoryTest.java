package ru.practikum.masters.goodsservice.product.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.brand.repository.BrandRepository;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.category.repository.CategoryRepository;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.product.model.ProductImage;
import ru.practikum.masters.goodsservice.product.model.ProductReview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("local")
class ProductReviewRepositoryTest {

    @Autowired
    private ProductReviewRepository reviewRepository;
    @Autowired
    private ProductImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;

    Category catElectronics = Category.createWithName("Electronics", null);
    Brand brand1 = Brand.createWithName("Dell");
    Brand brand2 = Brand.createWithName("Apple");
    Brand brand3 = Brand.createWithName("Canon");
    Product product1 = Product.create(
            "LAP-123",
            "Laptop",
            "High-performance laptop",
            BigDecimal.valueOf(999.99),
            catElectronics,
            brand1,
            10,
            new ArrayList<>() {
            });

    Product product2 = Product.create(
            "PH-001",
            "Phone",
            "High-performance laptop",
            BigDecimal.valueOf(10),
            catElectronics,
            brand2,
            5,
            new ArrayList<>() {
            });

    Product product3 = Product.create(
            "CA-002",
            "Camera",
            "High-performance laptop",
            BigDecimal.valueOf(300),
            catElectronics,
            brand3,
            8,
            new ArrayList<>() {
            });

    ProductImage productImage1 = ProductImage.createWithName(
            product1,
            "https://example.com/laptop.jpg",
            true
    );

    ProductImage productImage2 = ProductImage.createWithName(
            product2,
            "https://example.com/phone1.jpg",
            true
    );

    ProductImage productImage3 = ProductImage.createWithName(
            product2,
            "https://example.com/phone2.jpg",
            false
    );

    ProductReview productReview1 = ProductReview.createWithName(
            product1,
            UUID.randomUUID(),
            4.5F,
            "Performs well for daily tasks."
    );

    ProductReview productReview2 = ProductReview.createWithName(
            product1,
            UUID.randomUUID(),
            4.0F,
            "Solid performance."
    );

    @BeforeEach
    void setUp() {

        categoryRepository.save(catElectronics);
        brandRepository.save(brand1);
        brandRepository.save(brand2);
        brandRepository.save(brand3);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        imageRepository.save(productImage1);
        imageRepository.save(productImage2);
        imageRepository.save(productImage3);
        reviewRepository.save(productReview1);
        reviewRepository.save(productReview2);
    }
    @Test
    void save_shouldBeSaveReviewById() {

        assertThat(productReview1.getId()).isNotNull();
    }

    @Test
    void findById_shouldBeFoundReviewById() {

        Optional<ProductReview> found = reviewRepository.findById(productReview1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProduct().getName()).isEqualTo("Laptop");
        assertThat(found.get().getComment()).isEqualTo("Performs well for daily tasks.");
        assertThat(found.get().getRating()).isEqualTo(4.5F);
    }

    @Test
    void findByProductId_shouldBeFoundReviewsByProductId() {

        List<ProductReview> reviews = reviewRepository.findByProductId(product1.getId());

        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .extracting("comment")
                .containsExactly("Performs well for daily tasks.", "Solid performance.");
    }

    @Test
    void findByProductId_shouldReturnEmptyListWhenNoReviewsForProduct() {

        List<ProductReview> reviews = reviewRepository.findByProductId(product3.getId());
        assertThat(reviews).isEmpty();
    }

    @Test
    void shouldUpdateReview() {

        productReview1.setComment("Outstanding sound quality and design.");
        productReview1.setRating(4.8F);

        ProductReview updated = reviewRepository.save(productReview1);

        assertThat(updated.getComment()).isEqualTo("Outstanding sound quality and design.");
        assertThat(updated.getRating()).isEqualTo(4.8F);
    }

    @Test
    void deleteById_shouldDeleteReview() {

        UUID id = productReview1.getId();
        reviewRepository.deleteById(id);

        Boolean exists = reviewRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_shouldCheckIfReviewExistsById() {

        Boolean exists = reviewRepository.existsById(productReview1.getId());
        assertThat(exists).isTrue();

        Boolean notExists = reviewRepository.existsById(UUID.randomUUID());
        assertThat(notExists).isFalse();
    }
}
