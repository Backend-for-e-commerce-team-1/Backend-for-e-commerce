package ru.practikum.masters.goodsservice.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class ProductFilterRequest {
    @Min(value = 1, message = "Page must be 1 or greater")
    private Integer page = 1;
    @Min(value = 0, message = "Offset must be zero or positive")
    private Integer offset = 0;

    @Min(value = 1, message = "Limit must be positive")
    private Integer limit = 20;

    private String sort;

    private String category;

    @PositiveOrZero(message = "priceMin must be zero or positive")
    private Double priceMin;

    @PositiveOrZero(message = "priceMax must be zero or positive")
    private Double priceMax;

    private String brand;

    @AssertTrue(message = "priceMin must be less than or equal to priceMax")
    public boolean isPriceRangeValid() {
        if (priceMin == null || priceMax == null) {
            return true;
        }
        return Double.compare(priceMin, priceMax) <= 0;
    }
}