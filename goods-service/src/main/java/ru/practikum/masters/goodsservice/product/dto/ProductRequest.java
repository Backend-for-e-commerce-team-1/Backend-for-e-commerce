package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@Schema(description = "Запрос для создания или обновления товара")
public class ProductRequest {

    @Schema(
            description = "Название товара",
            example = "Смартфон Samsung Galaxy S23",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(
            description = "Артикул (код) товара",
            example = "YA-1234545"
    )
    private String code;

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
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Schema(
            description = "Идентификатор категории",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID categoryId;

    @Schema(
            description = "Название категории",
            example = "Смартфоны"
    )
    private String categoryName;

    @Schema(
            description = "Идентификатор бренда",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID brandId;

    @Schema(
            description = "Название бренда",
            example = "Samsung"
    )
    private String brandName;

    @Schema(
            description = "Ссылки на изображения товара",
            example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]"
    )
    private List<String> images;
}
