package ru.practikum.masters.goodsservice.product.dto;

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
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String code;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private UUID categoryId;
    private String categoryName;

    private UUID brandId;
    private String brandName;

    private List<String> images;
}
