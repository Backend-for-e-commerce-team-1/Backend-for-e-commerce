package ru.practikum.masters.cartservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class AddItemRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer quantity;
}
