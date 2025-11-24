package ru.practikum.masters.goodsservice.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ProductDeleteResponse {
    private final String message;
    private final String status;
    private final UUID id;
}