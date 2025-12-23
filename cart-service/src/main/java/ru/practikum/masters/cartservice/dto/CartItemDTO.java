package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Корзина покупок")
public class CartItemDTO {

    @Schema(
            description = "Идентификатор элемента в корзине",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID itemId;

    @Schema(
            description = "Идентификатор товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID productId;

    @Schema(
            description = "Название товара",
            example = "Смартфон Nokia 3310"
    )
    private String name;

    @Schema(
            description = "Количество товара",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer quantity;

    @Schema(
            description = "Цена за единицу товара",
            example = "1999.99"
    )
    private Double price;

    @Schema(
            description = "Общая стоимость позиции",
            example = "159999.98"
    )
    private Double totalPrice;
}
