package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@Schema(description = "Ответ на операцию удаления товара")
public class ProductDeleteResponse {

    @Schema(
            description = "Сообщение о результате операции",
            example = "Товар успешно удален",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String message;

    @Schema(
            description = "Статус операции",
            example = "DELETED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String status;

    @Schema(
            description = "Идентификатор удаленного товара",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final UUID id;
}