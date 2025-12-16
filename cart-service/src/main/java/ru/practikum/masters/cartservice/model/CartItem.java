package ru.practikum.masters.cartservice.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {

    UUID id;

    UUID productId;

    String name;

    Double price;

    Integer quantity;

    Double getTotalPrice() {
        return price * quantity;
    }

}
