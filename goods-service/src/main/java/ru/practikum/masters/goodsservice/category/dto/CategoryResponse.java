package ru.practikum.masters.goodsservice.category.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CategoryResponse {
    private final UUID id;
    private final String name;
}