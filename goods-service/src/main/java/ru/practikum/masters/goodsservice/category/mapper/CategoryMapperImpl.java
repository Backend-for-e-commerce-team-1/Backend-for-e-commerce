package ru.practikum.masters.goodsservice.category.mapper;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;
import ru.practikum.masters.goodsservice.category.model.Category;

@Component
@Slf4j
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryResponse toResponse(Category entity) {
        final String tag = "CategoryMapperImpl.toResponse";
        log.debug("{}: Enter with params: entity={}", tag, entity);
        CategoryResponse response = CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
        log.info("{}: Category mapped to response: id={}", tag, response.getId());
        log.debug("{}: Exit with result: {}", tag, response);
        return response;
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        final String tag = "CategoryMapperImpl.toEntity";
        log.debug("{}: Enter with params: request={}", tag, request);
        Category category = Category.createWithName(request.getName());
        log.info("{}: Category entity created with name={}", tag, category.getName());
        log.debug("{}: Exit with result: {}", tag, category);
        return category;
    }
}