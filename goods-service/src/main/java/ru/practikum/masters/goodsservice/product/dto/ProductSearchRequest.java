package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Параметры поиска товаров")
public class ProductSearchRequest extends ProductFilterRequest {

    @Schema(
            description = "Поисковый запрос",
            example = "Samsung Galaxy",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "query must be non-blank")
    private String query;

    @Schema(
            description = "Поля для поиска",
            example = "[\"name\", \"description\", etc]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "fields must be non-empty")
    private List<String> fields;
}