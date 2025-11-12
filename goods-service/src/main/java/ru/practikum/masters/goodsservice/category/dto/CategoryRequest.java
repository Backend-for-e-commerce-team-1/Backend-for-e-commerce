package ru.practikum.masters.goodsservice.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
}