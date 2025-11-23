package ru.practikum.masters.goodsservice.brand.mapper;

import org.springframework.stereotype.Component;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.model.Brand;

@Component
public class BrandMapperImpl implements BrandMapper {
    @Override
    public BrandResponse toResponse(Brand entity) {
        return BrandResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public Brand toEntity(BrandRequest request) {
        return Brand.createWithName(request.getName());
    }
}