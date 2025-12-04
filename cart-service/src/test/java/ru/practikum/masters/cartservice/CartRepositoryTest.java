package ru.practikum.masters.cartservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ru.practikum.masters.cartservice.model.Cart;
import ru.practikum.masters.cartservice.repository.CartRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CartRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartRepository cartRepository;

    private UUID testUserId;
    private Cart testCart;
    private String expectedKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        testUserId = UUID.randomUUID();
        testCart = new Cart();
        testCart.setId(UUID.randomUUID());
        testCart.setUserId(testUserId);
        testCart.setItems(new HashMap<>());
        testCart.setLastUpdated(LocalDateTime.now());

        expectedKey = "cart:" + testUserId;
    }

    @Test
    void shouldUseGetCartKeyAndSetWithTTL() {

        // when
        cartRepository.save(testCart);

        // then
        verify(valueOperations, times(1))
                .set(
                        anyString(),
                        eq(testCart),
                        eq(7L),
                        eq(TimeUnit.DAYS)
                );
    }

    @Test
    void shouldReturnCartWhenExistsInRedis() throws Exception {

        // given
        when(valueOperations.get(expectedKey)).thenReturn(testCart);

        // when
        Cart result = cartRepository.findByUserId(testUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        verify(valueOperations, times(1))
                .get(expectedKey);
    }

    @Test
    void shouldReturnNullWhenCartNotFound() {

        // given
        when(valueOperations.get(expectedKey)).thenReturn(null);

        // when
        Cart result = cartRepository.findByUserId(testUserId);

        // then
        assertThat(result).isNull();
        verify(valueOperations, times(1))
                .get(expectedKey);
    }

    @Test
    void shouldCallRedisDeleteWithCorrectKey() {

        // when
        cartRepository.deleteByUserId(testUserId);

        // then
        verify(redisTemplate, times(1))
                .delete(eq(expectedKey));
    }
}