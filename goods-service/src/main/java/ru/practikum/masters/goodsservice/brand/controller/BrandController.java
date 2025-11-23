package ru.practikum.masters.goodsservice.brand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.service.BrandService;

import java.util.UUID;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(@Valid @RequestBody BrandRequest request) {
        return brandService.create(request);
    }

    @GetMapping
    public Page<BrandResponse> list(Pageable pageable) {
        return brandService.list(pageable);
    }

    @GetMapping("/{id}")
    public BrandResponse get(@PathVariable UUID id) {
        return brandService.get(id);
    }

    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) {
        return brandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        brandService.delete(id);
    }
}