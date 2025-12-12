package ru.practikum.masters.cartservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.cartservice.dto.*;
import ru.practikum.masters.cartservice.service.CartService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping()
    public ResponseEntity<CartResponse> getCart() {
        log.info("Поступил запрос GET: /cart на получение текущего состояния корзины.");
        CartResponse response = cartService.getCart();
        log.info("Получено текущее состояние корзины {}.", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping()
    public ResponseEntity<CartResponse> clearCart() {
        log.info("Поступил запрос на очистку корзины пользователя.");
        CartResponse response = cartService.clearCart();
        log.info("Корзина пользователя успешно очищена.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("int/{userId}")
    public ClearCartResponse clearCartForUser(@PathVariable UUID userId) {
        log.info("Поступила команда на удаление пользователя, userId:" + userId);
        return cartService.clearCartForUser(userId);
    }
}
