package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Корзина покупок")
public class CartItemResponse {

    @Schema(
            description = "Идентификатор элемента в корзине",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID item_id;

    @Schema(
            description = "Идентификатор товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID product_id;

    @Schema(
            description = "Название товара",
            example = "Смартфон Nokia 3310"
    )
    private String name;

    @Schema(
            description = "Цена за единицу товара",
            example = "1999.99"
    )
    private Double price;

    @Schema(
            description = "Количество товара",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer quantity;

    @Schema(
            description = "Общая стоимость позиции",
            example = "159999.98"
    )
    private Double total_price;
}
