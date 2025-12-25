package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Параметры фильтрации для списка товаров")
public class ProductFilterRequest {

    @Schema(
            description = "Номер страницы",
            example = "1",
            minimum = "1",
            defaultValue = "1"
    )
    @Min(value = 1, message = "Page must be 1 or greater")
    private Integer page = 1;

    @Schema(
            description = "Смещение от начала списка",
            example = "0",
            minimum = "0",
            defaultValue = "0"
    )
    @Min(value = 0, message = "Offset must be zero or positive")
    private Integer offset = 0;

    @Schema(
            description = "Количество товаров на странице",
            example = "20",
            minimum = "1",
            defaultValue = "20",
            maximum = "100"
    )
    @Min(value = 1, message = "Limit must be positive")
    private Integer limit = 20;


    @Schema(
            description = "Поле для сортировки",
            example = "price",
            allowableValues = {"price", "name", "createdAt"}
    )
    private String sort;

    @Schema(
            description = "Фильтр по категории",
            example = "Смартфоны"
    )
    private String category;

    @Schema(
            description = "Минимальная цена",
            example = "10000.00"
    )
    @PositiveOrZero(message = "priceMin must be zero or positive")
    private BigDecimal priceMin;

    @Schema(
            description = "Максимальная цена",
            example = "100000.00"
    )
    @PositiveOrZero(message = "priceMax must be zero or positive")
    private BigDecimal priceMax;

    @Schema(
            description = "Фильтр по бренду",
            example = "Samsung"
    )
    private String brand;

    @AssertTrue(message = "priceMin must be less than or equal to priceMax")
    public boolean isPriceRangeValid() {
        if (priceMin == null || priceMax == null) {
            return true;
        }
        return priceMin.compareTo(priceMax) <= 0;
    }
}