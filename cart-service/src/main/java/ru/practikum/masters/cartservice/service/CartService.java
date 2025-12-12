package ru.practikum.masters.cartservice.service;

import ru.practikum.masters.cartservice.dto.*;

import java.util.UUID;

public interface CartService {

    CartResponse getCart();

    CartResponse clearCart();

    ClearCartResponse clearCartForUser(UUID userId);
}
