package ru.practikum.masters.cartservice.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Cart {
    private UUID userId;
    private Map<Long, Integer> items = new HashMap<>();
    private Double totalPrice;
}
