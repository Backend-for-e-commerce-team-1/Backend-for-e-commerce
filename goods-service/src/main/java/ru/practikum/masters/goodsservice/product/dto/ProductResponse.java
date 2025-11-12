package ru.practikum.masters.goodsservice.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ProductResponse {
    private final UUID productId;
    private final String code;
    private final String name;
    private final BigDecimal price;
    private final String category;
    private final String brand;
    private final List<String> images;
}
