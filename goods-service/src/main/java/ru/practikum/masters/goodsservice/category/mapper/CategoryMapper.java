package ru.practikum.masters.goodsservice.category.mapper;

import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;
import ru.practikum.masters.goodsservice.category.model.Category;

public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);

    Category toEntity(CategoryRequest request);
}