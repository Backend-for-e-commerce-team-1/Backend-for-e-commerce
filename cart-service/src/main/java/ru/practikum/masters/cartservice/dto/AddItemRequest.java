package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Запрос на добавление товара в корзину пользователя")
public class AddItemRequest {

    @Schema(
            description = "Уникальный идентификатор товара из каталога",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "uuid"
    )
    @NotNull
    private UUID productId;

    @Schema(
            description = "Количество товара для добавления в корзину",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer quantity;
}
