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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("local")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;

    Category catElectronics = Category.createWithName("Electronics", null);
    Category catBooks = Category.createWithName("Books", null);

    Brand brand1= Brand.createWithName("Dell");
    Brand brand2 = Brand.createWithName("Logitech");

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
            "KB-002",
            "Keyboard",
            "High-performance laptop",
            BigDecimal.valueOf(99.99),
            catElectronics,
            brand1,
            17,
            new ArrayList<>() {
            });
    Product product3 = Product.create(
            "MO-001",
            "Mouse",
            "High-performance laptop",
            BigDecimal.valueOf(9.99),
            catElectronics,
            brand2,
            25,
            new ArrayList<>() {
            });

    Product product4 = Product.create(
            "BK-001",
            "Book",
            "High-performance book",
            BigDecimal.valueOf(44.00),
            catBooks,
            brand2,
            55,
            new ArrayList<>() {
            });

    @BeforeEach
    void setUp() {

        categoryRepository.save(catElectronics);
        categoryRepository.save(catBooks);
        brandRepository.save(brand1);
        brandRepository.save(brand2);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
    }


    @Test
    void save_shouldBeSaveProduct() {

        assertThat(product1.getId()).isNotNull();
    }

    @Test
    void findById_shouldBeFoundProduct() {

        Optional<Product> found = productRepository.findById(product1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Laptop");
        assertThat(found.get().getPrice()).isEqualTo(BigDecimal.valueOf(999.99));
        assertThat(found.get().getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    void findByCategoryId_shouldFindProductsByCategoryId() {

        List<Product> electronicsProducts = productRepository.findByCategoryId(catElectronics.getId());
        assertThat(electronicsProducts).hasSize(3);
        assertThat(electronicsProducts)
                .extracting("name")
                .containsExactly("Laptop", "Keyboard", "Mouse");
    }

    @Test
    void findByBrand_shouldFindProductsByBrand() {

        List<Product> dellProducts = productRepository.findByBrand(brand1);
        assertThat(dellProducts).hasSize(2);
        assertThat(dellProducts)
                .extracting("brand")
                .allMatch(brand -> brand.equals(brand1));
    }

    @Test
    void findByCode_shouldFindProductByCode() {

        Optional<Product> found = productRepository.findByCode("LAP-123");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void findByCode_shouldReturnEmptyWhenCodeNotFound() {

        Optional<Product> found = productRepository.findByCode("NON-EXISTING");
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldDeleteProduct() {

        productRepository.deleteById(product1.getId());

        Boolean exists = productRepository.existsById(product1.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_shouldCheckIfProductExistsById() {

        Boolean exists = productRepository.existsById(product1.getId());
        assertThat(exists).isTrue();

        Boolean notExists = productRepository.existsById(UUID.randomUUID());
        assertThat(notExists).isFalse();
    }
}
