package ru.practikum.masters.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ после удаления корзины")
public class RemoveFromCartResponse {

    @Schema(
            description = "Сообщение о результате операции",
            example = "Корзина успешно удалена"
    )
    private String message;
}
