package ru.practikum.masters.cartservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.cartservice.dto.*;
import ru.practikum.masters.cartservice.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(
        name = "Cart Controller",
        description = "Управление корзиной покупок пользователя. Позволяет добавлять, удалять и изменять товары в корзине."
)
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Просмотр содержимого корзины",
            description = "Возвращает текущее состояние корзины пользователя со всеми товарами, их количеством и общей стоимостью."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корзина успешно получена",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Корзина не найдена или пользователь не авторизован"
            )
    })
    @GetMapping()
    public ResponseEntity<CartResponse> getCart() {
        log.info("Поступил запрос GET: /cart на получение текущего состояния корзины.");
        CartResponse response = cartService.getCart();
        log.info("Получено текущее состояние корзины {}.", response);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Очистка корзины",
            description = "Полностью очищает корзину текущего пользователя, удаляя все товары."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корзина успешно очищена",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Корзина не найдена"
            )
    })
    @DeleteMapping()
    public ResponseEntity<CartResponse> clearCart() {
        log.info("Поступил запрос на очистку корзины пользователя.");
        CartResponse response = cartService.clearCart();
        log.info("Корзина пользователя успешно очищена.");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Очистка корзины пользователя",
            description = "Полностью очищает корзину указанного пользователя. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корзина пользователя успешно очищена",
                    content = @Content(schema = @Schema(implementation = ClearCartResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @DeleteMapping("int/{userId}")
    public ClearCartResponse clearCartForUser(@PathVariable UUID userId) {
        log.info("Поступила команда на удаление пользователя, userId:" + userId);
        return cartService.clearCartForUser(userId);
    }

    @Operation(
            summary = "Удаление товара из корзины",
            description = "Удаляет конкретный товар из корзины пользователя по его идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар успешно удален из корзины",
                    content = @Content(schema = @Schema(implementation = RemoveFromCartResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден в корзине"
            )
    })
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<RemoveFromCartResponse> removeItemFromCart(
            @PathVariable UUID itemId) {

        log.info("DELETE /cart/items/{} - removing item from cart", itemId);

        RemoveFromCartResponse response = cartService.removeFromCart(itemId);

        if (response.getMessage().contains("not found")) {
            log.warn("Item {} not found in cart", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        log.info("Item {} successfully removed from cart", itemId);
        return ResponseEntity.ok(response);
    }
}
