package ru.practikum.masters.goodsservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;
import ru.practikum.masters.goodsservice.category.mapper.CategoryMapper;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.category.repository.CategoryRepository;
import ru.practikum.masters.goodsservice.common.exception.ConflictException;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.product.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        log.debug("Enter with params: request={}", request);
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                log.error("Conflict - category name exists: {}", request.getName());    
                throw new ConflictException("Category with the same name already exists");
            }
            Category category = categoryMapper.toEntity(request);
            log.debug("Mapped entity: {}", category);
            category = categoryRepository.save(category);
            log.info("Successfully created category, id={}", category.getId());
            CategoryResponse response = categoryMapper.toResponse(category);
            log.debug("Exit with result: {}", response);
            return response;
    }

    @Override
    public Page<CategoryResponse> list(Pageable pageable) {
        log.debug("Enter with params: pageable={}", pageable);
            Page<CategoryResponse> page = categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
            log.info("Fetched categories page, totalElements={}", page.getTotalElements());
            log.debug("Exit with result page size={}", page.getContent().size());
            return page;
    }

    @Override
    public CategoryResponse get(UUID id) {
        log.debug("Enter with params: id={}", id);
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("NotFound - category id={}", id);
                        return new NotFoundException("Category not found");
                    });
            CategoryResponse response = categoryMapper.toResponse(category);
            log.info("Category fetched, id={}", category.getId());
            log.debug("Exit with result: {}", response);
            return response;
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        log.debug("Enter with params: id={}, request={}", id, request);
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("NotFound - category id={}", id);
                        return new NotFoundException("Category not found");
                    });
            category.setName(request.getName());
            log.debug("Updated category name to: {}", request.getName());   
            category = categoryRepository.save(category);
            log.info("Category updated, id={}", category.getId());
            CategoryResponse response = categoryMapper.toResponse(category);
            log.debug("Exit with result: {}", response);
            return response;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Enter with params: id={}", id);
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("NotFound - category id={}", id);
                        return new NotFoundException("Category not found");
                    });
            long deps = productRepository.countByCategory_Id(id);
            log.debug("Dependent products count={}", deps);
            if (deps > 0) {
                log.error("Conflict - related products exist for category id={}", id);
                throw new ConflictException("Cannot delete category: related products exist");
            }
            categoryRepository.delete(category);
            log.info("Category deleted, id={}", category.getId());
            log.debug("Exit without result (void)");
}
}