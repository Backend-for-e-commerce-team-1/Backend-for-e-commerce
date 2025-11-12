package ru.practikum.masters.goodsservice.product.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductCreateResponse {
    private UUID productId;
    private String message;
}
