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
        cart.getItems().put(itemId, new CartItem(itemId, UUID.randomUUID(), "test name", 10.0, 1 ));

        when(cartRepository.findByUserId(any())).thenReturn(cart);
        cartService.getCart();

        verify(cartMapper, times(1)).toCartResponse(eq(cart), any());
    }

    @Test
    void getCart_shouldBeCreateEmptyCartWhenRedisNotReturnCart() {

        var userId = UUID.randomUUID();
        var cart = new Cart(userId);
        var itemId = UUID.randomUUID();
        cart.getItems().put(itemId, new CartItem(itemId, UUID.randomUUID(), "test name", 10.0, 1 ));

        when(cartRepository.findByUserId(any())).thenReturn(null);
        cartService.getCart();

        verify(cartMapper, times(1)).toCartResponse(any(Cart.class), any());
    }
}

