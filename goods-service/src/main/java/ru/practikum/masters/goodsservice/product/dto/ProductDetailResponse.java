package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@Schema(description = "Детальная информация о товаре")
public class ProductDetailResponse {

    @Schema(
            description = "Идентификатор товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID productId;

    @Schema(
            description = "Артикул (код) товара",
            example = "YA-12564",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;

    @Schema(
            description = "Название товара",
            example = "Смартфон Samsung Galaxy S23",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Описание товара",
            example = "Флагманский смартфон с процессором Snapdragon 8 Gen 2"
    )
    private String description;

    @Schema(
            description = "Цена товара",
            example = "79999.99",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal price;

    @Schema(
            description = "Название категории товара",
            example = "Смартфоны",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String category;

    @Schema(
            description = "Название бренда товара",
            example = "Samsung",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String brand;

    @Schema(
            description = "Ссылки на изображения товара",
            example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]"
    )
    private List<String> images;
}