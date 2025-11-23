package ru.practikum.masters.goodsservice.brand.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;

import java.util.UUID;

public interface BrandService {
    BrandResponse create(BrandRequest request);

    Page<BrandResponse> list(Pageable pageable);

    BrandResponse get(UUID id);

    BrandResponse update(UUID id, BrandRequest request);

    void delete(UUID id);
}