package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Ответ с содержимым корзины пользователя")
public class CartResponse {

    @Schema(description = "Список товаров в корзине")
    private List<CartItemDTO> items;

    @Schema(
            description = "Общая сумма корзины",
            example = "159999.98"
    )
    private Double totalAmount;
}
