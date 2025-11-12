package ru.practikum.masters.goodsservice.product.service;

import ru.practikum.masters.goodsservice.product.dto.*;

import java.util.UUID;

public interface ProductService {
    ProductCreateResponse create(ProductRequest productRequest);

    ProductListResponse getProducts(ProductFilterRequest filterRequest);

    ProductDetailResponse get(UUID id);

    ProductListResponse search(ProductSearchRequest request);

    ProductDeleteResponse delete(UUID id);
}
