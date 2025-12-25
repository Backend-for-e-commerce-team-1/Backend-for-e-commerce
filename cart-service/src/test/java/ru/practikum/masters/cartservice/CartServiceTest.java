package ru.practikum.masters.cartservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.masters.securitylib.service.SecurityContextService;
import ru.practikum.masters.cartservice.mapper.CartMapper;
import ru.practikum.masters.cartservice.model.Cart;
import ru.practikum.masters.cartservice.model.CartItem;
import ru.practikum.masters.cartservice.repository.CartRepository;
import ru.practikum.masters.cartservice.service.CartServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private SecurityContextService securityContextService;

    @Mock
    private CartMapper cartMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        cartService = new CartServiceImpl(cartRepository, cartMapper, securityContextService);
    }

    @Test
    void getCart_shouldBeUseSecurityContextService() {

        var userId = UUID.randomUUID();

        when(securityContextService.getCurrentUserId()).thenReturn(userId);

        cartService.getCart();

        verify(securityContextService, times(1))
                .getCurrentUserId();

        verify(cartRepository, times(1)).findByUserId(eq(userId));
    }

    @Test
    void getCart_shouldBeUseCartRepository() {

        var userId = UUID.randomUUID();
        var cart = new Cart(userId);
        var itemId = UUID.randomUUID();
        cart.getItems().put(itemId, new CartItem(itemId, UUID.randomUUID(), "test name", 10.0, 1));

        when(cartRepository.findByUserId(any())).thenReturn(cart);
        cartService.getCart();

        verify(cartMapper, times(1)).toCartResponse(eq(cart), any());
    }

    @Test
    void getCart_shouldBeCreateEmptyCartWhenRedisNotReturnCart() {

        var userId = UUID.randomUUID();
        var cart = new Cart(userId);
        var itemId = UUID.randomUUID();
        cart.getItems().put(itemId, new CartItem(itemId, UUID.randomUUID(), "test name", 10.0, 1));

        when(cartRepository.findByUserId(any())).thenReturn(null);
        cartService.getCart();

        verify(cartMapper, times(1)).toCartResponse(any(Cart.class), any());
    }

    @Test
    void clearCart_shouldBeUseSecurityContextService() {

        var userId = UUID.randomUUID();
        when(securityContextService.getCurrentUserId()).thenReturn(userId);

        cartService.clearCart();

        verify(securityContextService, times(1)).getCurrentUserId();
        verify(cartRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void clearCart_shouldBeSaveNewCartAutoClear() {

        cartService.clearCart();

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void getCart_shouldBeMapEmptyCartItemsList() {
        var userId = UUID.randomUUID();
        var cart = new Cart(userId);

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        cartService.getCart();

        verify(cartMapper).toCartResponse(eq(cart), eq(Collections.emptyList()));
    }

    @Test
    void removeFromCart_shouldBeRemoveItemWhenExists() {
        // given
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var cart = new Cart(userId);
        var cartItem = new CartItem(itemId, productId, "Test", 100.0, 2);
        cart.getItems().put(itemId, cartItem);

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        // when
        var response = cartService.removeFromCart(itemId);

        // then
        assertThat(response.getMessage()).isEqualTo("Item removed successfully");
        verify(cartRepository).save(argThat(c ->
                !c.getItems().containsKey(itemId) &&
                        c.getUserId().equals(userId)
        ));
    }

    @Test
    void removeFromCart_shouldBeReturnNotFoundWhenItemNotInCart() {
        // given
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();

        var cart = new Cart(userId);

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        // when
        var response = cartService.removeFromCart(itemId);

        // then
        assertThat(response.getMessage()).isEqualTo("Item not found in cart");
        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeFromCart_shouldBeReturnNotFoundWhenCartNotExists() {
        // given
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(null);

        // when
        var response = cartService.removeFromCart(itemId);

        // then
        assertThat(response.getMessage()).isEqualTo("Cart not found for user");
        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeFromCart_shouldBeUpdateLastUpdatedTimestamp() {
        // given
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var cart = new Cart(userId);
        var cartItem = new CartItem(itemId, productId, "Test", 100.0, 1);
        cart.getItems().put(itemId, cartItem);

        var beforeTime = LocalDateTime.now().minusSeconds(1);

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        // when
        cartService.removeFromCart(itemId);

        // then
        verify(cartRepository).save(argThat(c ->
                c.getLastUpdated() != null &&
                        c.getLastUpdated().isAfter(beforeTime)
        ));
    }
}