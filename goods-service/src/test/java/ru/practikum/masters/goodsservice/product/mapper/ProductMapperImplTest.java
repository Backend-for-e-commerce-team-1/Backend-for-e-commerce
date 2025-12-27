package ru.practikum.masters.goodsservice.product.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.product.dto.ProductCreateResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductDetailResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductListResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductResponse;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.product.model.ProductImage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperImplTest {

    private final ProductMapperImpl mapper = new ProductMapperImpl();

    @Test
    void toResponse_mapsBasicFields_andHandlesNullImages() {
        Category category = Category.createWithName("Electronics");
        Brand brand = Brand.createWithName("Acme");
        Product product = Product.create("C-1", "Phone", "Desc", BigDecimal.valueOf(499.99), category, brand, null);
        UUID id = UUID.randomUUID();
        product.setId(id);

        ProductResponse response = mapper.toResponse(product);

        assertThat(response.getProductId()).isEqualTo(id);
        assertThat(response.getCode()).isEqualTo("C-1");
        assertThat(response.getName()).isEqualTo("Phone");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(499.99));
        assertThat(response.getCategory()).isEqualTo("Electronics");
        assertThat(response.getBrand()).isEqualTo("Acme");
        assertThat(response.getImages()).isEmpty();
    }

    @Test
    void toDetailResponse_mapsAllFields() {
        Category category = Category.createWithName("Electronics");
        Brand brand = Brand.createWithName("Acme");
        Product product = Product.create("C-2", "Laptop", "Powerful laptop", BigDecimal.valueOf(1299.00), category, brand, List.of("https://img/1.png"));
        UUID id = UUID.randomUUID();
        product.setId(id);

        ProductDetailResponse response = mapper.toDetailResponse(product);

        assertThat(response.getProductId()).isEqualTo(id);
        assertThat(response.getCode()).isEqualTo("C-2");
        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getDescription()).isEqualTo("Powerful laptop");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(1299.00));
        assertThat(response.getCategory()).isEqualTo("Electronics");
        assertThat(response.getBrand()).isEqualTo("Acme");
        assertThat(response.getImages()).containsExactly("https://img/1.png");
    }

    @Test
    void toEntity_mapsRequestToProduct() {
        Category category = Category.createWithName("Electronics");
        Brand brand = Brand.createWithName("Acme");
        ProductRequest request = ProductRequest.builder()
                .code("C-3")
                .name("Camera")
                .description("DSLR camera")
                .price(BigDecimal.valueOf(799.50))
                .images(List.of("https://img/2.png"))
                .build();

        Product product = mapper.toEntity(request, category, brand);

        assertThat(product.getCode()).isEqualTo("C-3");
        assertThat(product.getName()).isEqualTo("Camera");
        assertThat(product.getDescription()).isEqualTo("DSLR camera");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(799.50));
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getBrand()).isSameAs(brand);
        assertThat(product.getImageUrls().stream().map(ProductImage::getImageUrl).toList()).containsExactly("https://img/2.png");
    }

    @Test
    void toProductListResponse_buildsPaginationInfo() {
        Category category = Category.createWithName("Electronics");
        Brand brand = Brand.createWithName("Acme");
        Product p1 = Product.create("C-1", "Phone", "Desc", BigDecimal.TEN, category, brand, List.of());
        Product p2 = Product.create("C-2", "Laptop", "Desc", BigDecimal.ONE, category, brand, List.of());
        p1.setId(UUID.randomUUID());
        p2.setId(UUID.randomUUID());

        PageRequest pageable = PageRequest.of(0, 20);
        PageImpl<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        ProductFilterRequest filter = new ProductFilterRequest();

        ProductListResponse list = mapper.toProductListResponse(page, filter);

        assertThat(list.getProducts()).hasSize(2);
        assertThat(list.getPagination().getCurrentPage()).isEqualTo(1);
        assertThat(list.getPagination().getTotalPages()).isEqualTo(1);
        assertThat(list.getPagination().getTotalItems()).isEqualTo(2L);
    }

    @Test
    void toShortResponse_returnsIdAndMessage() {
        UUID id = UUID.randomUUID();
        ProductCreateResponse response = mapper.toShortResponse(id, "Created");
        assertThat(response.getProductId()).isEqualTo(id);
        assertThat(response.getMessage()).isEqualTo("Created");
    }
}