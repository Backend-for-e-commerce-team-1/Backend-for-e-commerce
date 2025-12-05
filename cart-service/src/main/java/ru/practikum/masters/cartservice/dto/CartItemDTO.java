package ru.practikum.masters.cartservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    @NotNull
    private UUID itemId;
    @NotNull
    private UUID productId;
    private String name;
    @NotNull
    @Positive
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
