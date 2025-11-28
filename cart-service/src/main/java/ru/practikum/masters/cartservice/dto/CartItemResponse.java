package ru.practikum.masters.cartservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID item_id;
    private UUID product_id;
    private String name;
    private Double price;
    private Integer quantity;
    private Double total_price;
}
