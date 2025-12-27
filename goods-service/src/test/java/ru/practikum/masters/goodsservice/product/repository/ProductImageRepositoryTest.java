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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("local")
class ProductImageRepositoryTest {

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
    }

    @Test
    void save_shouldBeSaveImageById() {

        assertThat(productImage1.getId()).isNotNull();
    }

    @Test
    void findById_shouldBeFoundImageById() {

        Optional<ProductImage> found = imageRepository.findById(productImage1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProduct().getName()).isEqualTo("Laptop");
        assertThat(found.get().getImageUrl()).isEqualTo("https://example.com/laptop.jpg");
        assertThat(found.get().getIsPrimary()).isTrue();
    }

    @Test
    void findByProductId_shouldBeFoundImagesByProductId() {

        List<ProductImage> images = imageRepository.findByProductId(product2.getId());

        assertThat(images).hasSize(2);
        assertThat(images)
                .extracting("imageUrl")
                .containsExactly("https://example.com/phone1.jpg", "https://example.com/phone2.jpg");

        assertThat(images.get(0).getIsPrimary()).isTrue();
        assertThat(images.get(1).getIsPrimary()).isFalse();
    }

    @Test
    void findByProductId_shouldReturnEmptyListWhenNoImagesForProduct() {

        List<ProductImage> images = imageRepository.findByProductId(product3.getId());
        assertThat(images).isEmpty();
    }

    @Test
    void shouldUpdateImage() {

        productImage1.setImageUrl("https://example.com/new.jpg");
        productImage1.setIsPrimary(true);

        ProductImage updated = imageRepository.save(productImage1);

        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/new.jpg");
        assertThat(updated.getIsPrimary()).isTrue();
    }

    @Test
    void deleteById_shouldDeleteImage() {

        UUID id = productImage1.getId();
        imageRepository.deleteById(id);

        Boolean exists = imageRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_shouldCheckIfImageExistsById() {

        Boolean exists = imageRepository.existsById(productImage3.getId());
        assertThat(exists).isTrue();

        Boolean notExists = imageRepository.existsById(UUID.randomUUID());
        assertThat(notExists).isFalse();
    }
}
