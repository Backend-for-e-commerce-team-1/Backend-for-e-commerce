package ru.practikum.masters.goodsservice.brand.mapper;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.model.Brand;

@Component
@Slf4j
public class BrandMapperImpl implements BrandMapper {
    @Override
    public BrandResponse toResponse(Brand entity) {
        final String tag = "BrandMapperImpl.toResponse";
        log.debug("{}: Enter with params: entity={}", tag, entity);
        BrandResponse response = BrandResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
        log.info("{}: Brand mapped to response: id={}", tag, response.getId());
        log.debug("{}: Exit with result: {}", tag, response);
        return response;
    }

    @Override
    public Brand toEntity(BrandRequest request) {
        final String tag = "BrandMapperImpl.toEntity";
        log.debug("{}: Enter with params: request={}", tag, request);
        Brand brand = Brand.createWithName(request.getName());
        log.info("{}: Brand entity created with name={}", tag, brand.getName());
        log.debug("{}: Exit with result: {}", tag, brand);
        return brand;
    }
}