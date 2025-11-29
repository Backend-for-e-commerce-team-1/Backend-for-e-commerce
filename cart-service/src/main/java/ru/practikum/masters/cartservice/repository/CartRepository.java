package ru.practikum.masters.cartservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.practikum.masters.cartservice.model.Cart;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CartRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private String getCartKey(UUID userId) {
        return "cart:" + userId.toString();
    }

    public void saveCart(Cart cart) {
        String key = getCartKey(cart.getUserId());
        redisTemplate.opsForValue().set(key, cart, 7, TimeUnit.DAYS);
    }

    public Cart findCartByUserId(UUID userId) {
        String key = getCartKey(userId);
        return (Cart) redisTemplate.opsForValue().get(key);
    }

    public void deleteCart(UUID userId) {
        String key = getCartKey(userId);
        redisTemplate.delete(key);
    }

    public boolean cartExists(UUID userId) {
        String key = getCartKey(userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
