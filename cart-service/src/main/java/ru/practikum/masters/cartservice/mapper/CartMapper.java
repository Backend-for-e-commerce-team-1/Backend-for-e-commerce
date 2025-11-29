package ru.practikum.masters.cartservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practikum.masters.cartservice.dto.AddItemRequest;
import ru.practikum.masters.cartservice.dto.CartItemResponse;
import ru.practikum.masters.cartservice.dto.CartResponse;
import ru.practikum.masters.cartservice.model.Cart;
import ru.practikum.masters.cartservice.model.CartItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product_id")
    CartItem toCartItem(AddItemRequest request);

    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "total_amount", source = "cart.totalPrice")
    CartResponse toCartResponse(Cart cart, List<CartItem> cartItems);

    @Mapping(target = "item_id", source = "productId")
    @Mapping(target = "product_id", source = "productId")
    @Mapping(target = "total_price", expression = "java(item.getPrice() * item.getQuantity())")
    CartItemResponse toCartItemResponse(CartItem item);
}
