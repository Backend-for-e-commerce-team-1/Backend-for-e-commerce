package ru.practikum.masters.goodsservice.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.product.dto.ProductRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductCreateResponse;
import ru.practikum.masters.goodsservice.product.service.ProductService;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCreateResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
