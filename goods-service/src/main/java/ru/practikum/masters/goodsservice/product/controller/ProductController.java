package ru.practikum.masters.goodsservice.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductSearchRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductListResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductDetailResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductDeleteResponce;
import ru.practikum.masters.goodsservice.product.service.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ProductListResponse getProducts(@Valid @ModelAttribute ProductFilterRequest filterRequest) {
        return productService.getProducts(filterRequest);
    }

    @GetMapping("/{id}")
    public ProductDetailResponse get(@PathVariable UUID id) {
        return productService.get(id);
    }

    @GetMapping("/search")
    public ProductListResponse search(@Valid @ModelAttribute ProductSearchRequest request) {
        return productService.search(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProductDeleteResponce delete(@PathVariable UUID id) {
        return productService.delete(id);
    }
}
