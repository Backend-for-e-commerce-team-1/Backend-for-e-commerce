package ru.practikum.masters.goodsservice.category.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;

import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);

    Page<CategoryResponse> list(Pageable pageable);

    CategoryResponse get(UUID id);

    CategoryResponse update(UUID id, CategoryRequest request);

    void delete(UUID id);
}