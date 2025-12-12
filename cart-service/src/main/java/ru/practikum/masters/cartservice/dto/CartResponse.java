package ru.practikum.masters.cartservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {

    private List<CartItemDTO> items;
    private Double totalAmount;
}
