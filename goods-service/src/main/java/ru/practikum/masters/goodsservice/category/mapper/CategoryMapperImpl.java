package ru.practikum.masters.goodsservice.category.mapper;

import org.springframework.stereotype.Component;
import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;
import ru.practikum.masters.goodsservice.category.model.Category;

@Component
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryResponse toResponse(Category entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        return Category.createWithName(request.getName());
    }
}