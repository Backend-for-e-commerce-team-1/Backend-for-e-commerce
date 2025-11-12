package ru.practikum.masters.goodsservice.brand.mapper;

import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.model.Brand;

public interface BrandMapper {
    BrandResponse toResponse(Brand entity);

    Brand toEntity(BrandRequest request);
}