package ru.practikum.masters.goodsservice.product.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDetailResponse {
    private UUID productId;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String brand;
    private List<String> images;
}