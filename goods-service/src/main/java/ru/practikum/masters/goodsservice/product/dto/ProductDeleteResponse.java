package ru.practikum.masters.goodsservice.product.dto;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDeleteResponce {
    private final String message;
    private final String status;
    private final UUID id;
}