package ru.practikum.masters.goodsservice.brand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Запрос для создания или обновления бренда")
public class BrandRequest {

    @Schema(
            description = "Название бренда",
            example = "Samsung",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Brand name is required")
    private String name;
}