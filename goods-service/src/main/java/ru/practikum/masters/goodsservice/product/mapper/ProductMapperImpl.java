package ru.practikum.masters.goodsservice.product.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.product.model.ProductImage;

import java.util.List;
import java.util.UUID;

@Component
public class ProductMapperImpl implements ProductMapper{
    @Override
    public ProductResponse toResponse(Product entity) {
            return ProductResponse.builder()
                    .productId(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .price(entity.getPrice())
                    .category(entity.getCategory().getName())
                    .brand(entity.getBrand().getName())
                    .images(entity.getImageUrls() != null ? entity.getImageUrls().stream().map(ProductImage::getImageUrl).toList() : java.util.List.of())
                    .build();
    }

    @Override
    public ProductDetailResponse toDetailResponse(Product entity) {
            return ProductDetailResponse.builder()
                    .productId(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .price(entity.getPrice())
                    .category(entity.getCategory().getName())
                    .brand(entity.getBrand().getName())
                    .images(entity.getImageUrls() != null ? entity.getImageUrls().stream().map(ProductImage::getImageUrl).toList() : List.of())
                    .build();
    }

    @Override
    public Product toEntity(ProductRequest request, Category category, Brand brand) {
            return Product.create(
                    request.getCode(),
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    category,
                    brand,
                    request.getImages()
            );
    }


    @Override
    public ProductListResponse toProductListResponse(Page<Product> productPage, ProductFilterRequest filter) {
            List<ProductResponse> productResponses = productPage.getContent().stream()
                    .map(this::toResponse)
                    .toList();

            ProductListResponse.PaginationInfo paginationInfo = buildPaginationInfo(productPage, filter);

            return ProductListResponse.builder()
                    .products(productResponses)
                    .pagination(paginationInfo)
                    .build();
    }

    @Override
    public ProductCreateResponse toShortResponse(UUID id, String message) {
        return ProductCreateResponse.builder()
                .productId(id)
                .message(message)
                .build();
    }

    private ProductListResponse.PaginationInfo buildPaginationInfo(Page<Product> page, ProductFilterRequest filter) {
        return ProductListResponse.PaginationInfo.builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .build();
    }
}
