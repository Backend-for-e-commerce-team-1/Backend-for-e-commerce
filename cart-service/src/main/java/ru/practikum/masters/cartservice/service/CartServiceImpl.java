package ru.practikum.masters.cartservice.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.masters.securitylib.service.SecurityContextService;
import ru.practikum.masters.cartservice.dto.*;
import ru.practikum.masters.cartservice.mapper.CartMapper;
import ru.practikum.masters.cartservice.model.Cart;
import ru.practikum.masters.cartservice.repository.CartRepository;

import java.util.UUID;

@Slf4j
@Data
@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final SecurityContextService securityContextService;

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart() {

        //Получить userId из SecurityContext
        UUID userId = securityContextService.getCurrentUserId();

        // Получить корзину из Redis
        var cart = cartRepository.findByUserId(userId);

        // Если корзина не найдена - вернуть пустую корзину
        if (cart == null) {
            cart = new Cart(userId);
        }

        // Маппинг Cart -> CartResponse
        return cartMapper.toCartResponse(cart, cart.getItems().values().stream().toList());
    }

    @Override
    public CartResponse clearCart() {

        //Получить userId из SecurityContext
        UUID userId = securityContextService.getCurrentUserId();

        // Удалить корзину из Redis
        clearCartForUser(userId);

        // После очистки GET /cart возвращает пустую корзину
        var cart = new Cart(userId);
        cartRepository.save(cart);

        // Маппинг Cart -> CartResponse
        return cartMapper.toCartResponse(cart, cart.getItems().values().stream().toList());
    }

    // Реализовать метод для внутреннего использования (будет вызываться из ORDER сервиса)
    @Override
    public ClearCartResponse clearCartForUser(UUID userId) {
        try {
            cartRepository.deleteByUserId(userId);
            return new ClearCartResponse();
        } catch (Exception e) {
            return new ClearCartResponse(e.getMessage());
        }
    }


}

