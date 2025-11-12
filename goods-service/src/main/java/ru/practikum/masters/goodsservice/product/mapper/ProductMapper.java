package ru.practikum.masters.goodsservice.product.mapper;

import org.springframework.data.domain.Page;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.model.Product;

import java.util.UUID;

public interface ProductMapper {
    ProductResponse toResponse(Product entity);

    ProductDetailResponse toDetailResponse(Product entity);

    Product toEntity(ProductRequest request, Category category, Brand brand);

    ProductListResponse toProductListResponse(Page<Product> page, ProductFilterRequest filter);

    ProductCreateResponse toShortResponse(UUID id, String message);
}
