package ru.practikum.masters.goodsservice.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.brand.repository.BrandRepository;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.category.repository.CategoryRepository;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;
import ru.practikum.masters.goodsservice.product.configuration.pagination.PageableFactory;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.mapper.ProductMapper;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.product.repository.ProductRepository;
import ru.practikum.masters.goodsservice.product.spec.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @Mock private ProductSpecification specification;
    @Mock private PageableFactory pageableFactory;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BrandRepository brandRepository;

    private ProductServiceImpl service;

    @BeforeEach
    void setup() {
        service = new ProductServiceImpl(
                productRepository,
                productMapper,
                specification,
                pageableFactory,
                categoryRepository,
                brandRepository
        );
    }

    @Test
    void create_withCategoryIdAndBrandId_success() {
        UUID categoryId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        Category category = Category.createWithName("Electronics");
        category.setId(categoryId);
        Brand brand = Brand.createWithName("Acme");
        brand.setId(brandId);

        ProductRequest request = ProductRequest.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(499.99))
                .categoryId(categoryId)
                .brandId(brandId)
                .code("P-001")
                .images(List.of("https://img/1.png"))
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        Product entity = Product.create(
                request.getCode(), request.getName(), request.getDescription(), request.getPrice(), category, brand, request.getImages()
        );
        UUID productId = UUID.randomUUID();
        entity.setId(productId);

        when(productMapper.toEntity(eq(request), eq(category), eq(brand))).thenReturn(entity);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0, Product.class));
        ProductCreateResponse expected = ProductCreateResponse.builder().productId(productId).message("Product created successfully.").build();
        when(productMapper.toShortResponse(productId, "Product created successfully.")).thenReturn(expected);

        ProductCreateResponse actual = service.create(request);

        assertThat(actual.getProductId()).isEqualTo(productId);
        assertThat(actual.getMessage()).isEqualTo("Product created successfully.");
        verify(categoryRepository).findById(categoryId);
        verify(brandRepository).findById(brandId);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toShortResponse(productId, "Product created successfully.");
    }

    @Test
    void create_withCategoryNameAndBrandName_success() {
        Category category = Category.createWithName("Electronics");
        Brand brand = Brand.createWithName("Acme");

        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .categoryName("Electronics")
                .brandName("Acme")
                .code("L-001")
                .build();

        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));
        when(brandRepository.findByNameIgnoreCase("Acme")).thenReturn(Optional.of(brand));

        Product entity = Product.create(request.getCode(), request.getName(), request.getDescription(), request.getPrice(), category, brand, request.getImages());
        UUID productId = UUID.randomUUID();
        entity.setId(productId);

        when(productMapper.toEntity(eq(request), eq(category), eq(brand))).thenReturn(entity);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0, Product.class));
        ProductCreateResponse expected = ProductCreateResponse.builder().productId(productId).message("Product created successfully.").build();
        when(productMapper.toShortResponse(productId, "Product created successfully.")).thenReturn(expected);

        ProductCreateResponse actual = service.create(request);

        assertThat(actual.getProductId()).isEqualTo(productId);
        assertThat(actual.getMessage()).isEqualTo("Product created successfully.");
        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(brandRepository).findByNameIgnoreCase("Acme");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_missingCategory_throwsValidationException() {
        ProductRequest request = ProductRequest.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(100))
                .brandId(UUID.randomUUID())
                .build();

        assertThrows(ValidationException.class, () -> service.create(request));
        verifyNoInteractions(productRepository);
    }

    @Test
    void create_categoryNotFound_throwsNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        ProductRequest request = ProductRequest.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(100))
                .categoryId(categoryId)
                .brandId(UUID.randomUUID())
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(request));
        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void create_brandNotFound_throwsNotFoundException() {
        UUID brandId = UUID.randomUUID();
        ProductRequest request = ProductRequest.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(100))
                .brandId(brandId)
                .categoryId(UUID.randomUUID())
                .build();

        when(categoryRepository.findById(any())).thenReturn(Optional.of(Category.createWithName("Cat")));
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(request));
        verify(brandRepository).findById(brandId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void get_success() {
        UUID id = UUID.randomUUID();
        Product product = Product.create("C", "Name", "Desc", BigDecimal.TEN, Category.createWithName("Cat"), Brand.createWithName("Brand"), List.of());
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        ProductDetailResponse expected = ProductDetailResponse.builder().productId(id).name("Name").code("C").build();
        when(productMapper.toDetailResponse(product)).thenReturn(expected);

        ProductDetailResponse actual = service.get(id);
        assertThat(actual.getProductId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Name");
        verify(productRepository).findById(id);
        verify(productMapper).toDetailResponse(product);
    }

    @Test
    void get_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(id));
        verify(productRepository).findById(id);
    }

    @Test
    void getProducts_success() {
        ProductFilterRequest filter = new ProductFilterRequest();
        Pageable pageable = PageRequest.of(0, 10);
        when(pageableFactory.createFromFilter(filter)).thenReturn(pageable);
        when(specification.buildFromFilter(filter)).thenReturn((root, q, cb) -> null);

        Product p = Product.create("C", "N", "D", BigDecimal.ONE, Category.createWithName("Cat"), Brand.createWithName("Brand"), List.of());
        p.setId(UUID.randomUUID());
        Page<Product> page = new PageImpl<>(List.of(p), pageable, 1);
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        ProductListResponse expected = ProductListResponse.builder()
                .products(List.of(ProductResponse.builder().productId(p.getId()).name("N").code("C").build()))
                .pagination(ProductListResponse.PaginationInfo.builder().currentPage(1).totalPages(1).totalItems(1L).build())
                .build();
        when(productMapper.toProductListResponse(page, filter)).thenReturn(expected);

        ProductListResponse actual = service.getProducts(filter);
        assertThat(actual.getProducts()).hasSize(1);
        assertThat(actual.getPagination().getTotalItems()).isEqualTo(1L);
        verify(pageableFactory).createFromFilter(filter);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
        verify(productMapper).toProductListResponse(page, filter);
    }

    @Test
    void search_validCsvFields_success() {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setQuery("ace");
        request.setFields(List.of("code, name"));
        request.setLimit(10);
        request.setOffset(0);

        Pageable pageable = PageRequest.of(0, 10);
        when(pageableFactory.createFromFilter(request)).thenReturn(pageable);

        Specification<Product> searchSpec = (root, q, cb) -> null;
        when(specification.search(any(ProductSearchRequest.class))).thenReturn(searchSpec);
        when(specification.buildFromFilter(request)).thenReturn((root, q, cb) -> null);

        Page<Product> page = new PageImpl<>(List.of(), pageable, 0);
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        ProductListResponse expected = ProductListResponse.builder()
                .products(List.of())
                .pagination(ProductListResponse.PaginationInfo.builder().currentPage(1).totalPages(0).totalItems(0L).build())
                .build();
        when(productMapper.toProductListResponse(page, request)).thenReturn(expected);

        ProductListResponse actual = service.search(request);
        assertThat(actual.getProducts()).isEmpty();
        verify(pageableFactory).createFromFilter(request);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
        verify(productMapper).toProductListResponse(page, request);
    }

    @Test
    void search_invalidFields_throwsValidationException() {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setQuery("q");
        request.setFields(List.of("invalid"));

        when(specification.search(any(ProductSearchRequest.class))).thenThrow(new ValidationException("invalid fields"));
        assertThrows(ValidationException.class, () -> service.search(request));
        verifyNoInteractions(productRepository);
    }

    @Test
    void search_emptyFields_throwsValidationException() {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setQuery("q");
        request.setFields(List.of());

        when(specification.search(any(ProductSearchRequest.class))).thenThrow(new ValidationException("empty fields"));
        assertThrows(ValidationException.class, () -> service.search(request));
        verifyNoInteractions(productRepository);
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();
        Product product = Product.create("C", "N", "D", BigDecimal.ONE, Category.createWithName("Cat"), Brand.createWithName("Brand"), List.of());
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductDeleteResponse resp = service.delete(id);
        assertThat(resp.getId()).isEqualTo(id);
        assertThat(resp.getMessage()).isEqualTo("Product deleted successfully");
        assertThat(resp.getStatus()).isEqualTo("DELETED");
        verify(productRepository).delete(product);
    }

    @Test
    void delete_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.delete(id));
        verify(productRepository).findById(id);
    }
}