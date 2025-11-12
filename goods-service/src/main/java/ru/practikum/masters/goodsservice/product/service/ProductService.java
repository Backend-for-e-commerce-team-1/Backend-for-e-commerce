package ru.practikum.masters.goodsservice.product.service;

import ru.practikum.masters.goodsservice.product.dto.*;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductCreateResponse create(ProductRequest productRequest);

    ProductListResponse getProducts(ProductFilterRequest filterRequest);

    ProductDetailResponse get(UUID id);

    ProductListResponse search(ProductSearchRequest request);

    ProductDeleteResponce delete(UUID id);
}
