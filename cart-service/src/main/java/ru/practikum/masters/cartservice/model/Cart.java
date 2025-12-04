package ru.practikum.masters.cartservice.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {

    UUID id;

    UUID userId;

    Map<UUID, CartItem> items = new HashMap<>();

    LocalDateTime lastUpdated;

    public double getTotalAmount() {
        return items.values()
                .stream()
                .mapToDouble(x -> x.getTotalPrice())
                .sum();
    }

}
