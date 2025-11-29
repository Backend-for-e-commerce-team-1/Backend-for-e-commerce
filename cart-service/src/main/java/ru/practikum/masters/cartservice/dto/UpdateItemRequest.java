package ru.practikum.masters.cartservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateItemRequest {
    @NotNull
    @Positive
    private Integer quantity;
}
