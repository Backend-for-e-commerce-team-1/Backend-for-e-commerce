package ru.practikum.masters.goodsservice.product.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "Ответ на успешное создание товара")
public class ProductCreateResponse {

    @Schema(
            description = "Идентификатор созданного товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID productId;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Товар успешно создан",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;
}
