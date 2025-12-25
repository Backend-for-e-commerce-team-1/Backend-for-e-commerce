package ru.practikum.masters.goodsservice.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Запрос для создания или обновления категории")
public class CategoryRequest {

    @Schema(
            description = "Название категории",
            example = "Товары народного потребления",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Category name is required")
    private String name;
}