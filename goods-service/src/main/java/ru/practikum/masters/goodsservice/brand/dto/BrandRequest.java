package ru.practikum.masters.goodsservice.brand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BrandRequest {
    @NotBlank(message = "Brand name is required")
    private String name;
}