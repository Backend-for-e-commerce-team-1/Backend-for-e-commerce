package ru.practikum.masters.goodsservice.product.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.model.Product;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ProductMapperImpl implements ProductMapper{
    @Override
    public ProductResponse toResponse(Product entity) {
        final String tag = "ProductMapperImpl.toResponse";
        log.debug("{}: Enter with params: entity={}", tag, entity);
        try {
            ProductResponse response = ProductResponse.builder()
                    .productId(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .price(entity.getPrice())
                    .category(entity.getCategory().getName())
                    .brand(entity.getBrand().getName())
                    .images(entity.getImageUrls() != null ? entity.getImageUrls() : java.util.List.of())
                    .build();
            log.info("{}: Product mapped to response: name={}", tag, response.getName());
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public ProductDetailResponse toDetailResponse(Product entity) {
        final String tag = "ProductMapperImpl.toDetailResponse";
        log.debug("{}: Enter with params: entity={}", tag, entity);
        try {
            ProductDetailResponse response = ProductDetailResponse.builder()
                    .productId(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .price(entity.getPrice())
                    .category(entity.getCategory().getName())
                    .brand(entity.getBrand().getName())
                    .images(entity.getImageUrls() != null ? entity.getImageUrls() : List.of())
                    .build();
            log.info("{}: Product mapped to detail response: name={}", tag, response.getName());
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public Product toEntity(ProductRequest request, Category category, Brand brand) {
        final String tag = "ProductMapperImpl.toEntity";
        log.debug("{}: Enter with params: request={}, category={}, brand={} ", tag, request, category, brand);
        try {
            Product product = Product.create(
                    request.getCode(),
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    category,
                    brand,
                    request.getImages()
            );
            log.info("{}: Product entity created: name={}", tag, product.getName());
            log.debug("{}: Exit with result: {}", tag, product);
            return product;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }


    @Override
    public ProductListResponse toProductListResponse(Page<Product> productPage, ProductFilterRequest filter) {
        final String tag = "ProductMapperImpl.toProductListResponse";
        log.debug("{}: Enter with params: pageTotal={}, filter={}", tag, productPage.getTotalElements(), filter);
        try {
            List<ProductResponse> productResponses = productPage.getContent().stream()
                    .map(this::toResponse)
                    .toList();
            log.debug("{}: Mapped product responses count={}", tag, productResponses.size());

            ProductListResponse.PaginationInfo paginationInfo = buildPaginationInfo(productPage, filter);
            log.debug("{}: Built pagination info: currentPage={}, totalPages={}, totalItems={}", tag,
                    paginationInfo.getCurrentPage(), paginationInfo.getTotalPages(), paginationInfo.getTotalItems());

            ProductListResponse result = ProductListResponse.builder()
                    .products(productResponses)
                    .pagination(paginationInfo)
                    .build();
            log.info("{}: Product list response created", tag);
            log.debug("{}: Exit with result: productsCount={}", tag, result.getProducts().size());
            return result;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public ProductCreateResponse toShortResponse(UUID id, String message) {
        return ProductCreateResponse.builder()
                .productId(id)
                .message(message)
                .build();
    }

    private ProductListResponse.PaginationInfo buildPaginationInfo(Page<Product> page, ProductFilterRequest filter) {
        final String tag = "ProductMapperImpl.buildPaginationInfo";
        ProductListResponse.PaginationInfo info = ProductListResponse.PaginationInfo.builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .build();
        log.debug("{}: PaginationInfo built: currentPage={}, totalPages={}, totalItems={}",
                tag, info.getCurrentPage(), info.getTotalPages(), info.getTotalItems());
        return info;
    }
}
