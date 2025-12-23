package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ после очистки корзины")
public class ClearCartResponse {

    @Schema(
            description = "Сообщение о результате операции",
            example = "Корзина успешно очищена"
    )
    private String message;
}
