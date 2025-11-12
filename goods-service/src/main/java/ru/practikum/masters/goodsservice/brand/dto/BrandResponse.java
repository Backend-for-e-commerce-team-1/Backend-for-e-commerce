package ru.practikum.masters.goodsservice.brand.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class BrandResponse {
    private final UUID id;
    private final String name;
}