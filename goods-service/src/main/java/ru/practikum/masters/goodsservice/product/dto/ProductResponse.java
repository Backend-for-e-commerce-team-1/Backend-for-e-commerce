package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Schema(description = "Информация (описание) о товаре")
public class ProductResponse {

    @Schema(
            description = "Идентификатор товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final UUID productId;

    @Schema(
            description = "Артикул (код) товара",
            example = "YA-1234545"
    )
    private final String code;

    @Schema(
            description = "Название товара",
            example = "Смартфон Samsung Galaxy S23",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String name;

    @Schema(
            description = "Цена товара",
            example = "79999.99",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final BigDecimal price;

    @Schema(
            description = "Название категории",
            example = "Смартфоны"
    )
    private final String category;

    @Schema(
            description = "Название бренда",
            example = "Samsung"
    )
    private final String brand;

    @Schema(
            description = "Ссылки на изображения товара",
            example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]"
    )
    private final List<String> images;
}
