package ru.practikum.masters.goodsservice.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@Schema(description = "Ответ с информацией о категории")
public class CategoryResponse {
    @Schema(
            description = "Уникальный идентификатор категории",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final UUID id;

    @Schema(
            description = "Название категории",
            example = "Товары народного потребления",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String name;
}