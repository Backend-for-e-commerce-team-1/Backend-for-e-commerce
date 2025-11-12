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
        final String tag = "CategoryServiceImpl.create";
        log.debug("{}: Enter with params: request={}", tag, request);
        try {
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                log.error("{}: Conflict - category name exists: {}", tag, request.getName());
                throw new ConflictException("Category with the same name already exists");
            }
            Category category = categoryMapper.toEntity(request);
            log.debug("{}: Mapped entity: {}", tag, category);
            category = categoryRepository.save(category);
            log.info("{}: Successfully created category, id={}", tag, category.getId());
            CategoryResponse response = categoryMapper.toResponse(category);
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<CategoryResponse> list(Pageable pageable) {
        final String tag = "CategoryServiceImpl.list";
        log.debug("{}: Enter with params: pageable={}", tag, pageable);
        try {
            Page<CategoryResponse> page = categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
            log.info("{}: Fetched categories page, totalElements={}", tag, page.getTotalElements());
            log.debug("{}: Exit with result page size={}", tag, page.getContent().size());
            return page;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public CategoryResponse get(UUID id) {
        final String tag = "CategoryServiceImpl.get";
        log.debug("{}: Enter with params: id={}", tag, id);
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - category id={}", tag, id);
                        return new NotFoundException("Category not found");
                    });
            CategoryResponse response = categoryMapper.toResponse(category);
            log.info("{}: Category fetched, id={}", tag, category.getId());
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        final String tag = "CategoryServiceImpl.update";
        log.debug("{}: Enter with params: id={}, request={}", tag, id, request);
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - category id={}", tag, id);
                        return new NotFoundException("Category not found");
                    });
            category.setName(request.getName());
            log.debug("{}: Updated category name to: {}", tag, request.getName());
            category = categoryRepository.save(category);
            log.info("{}: Category updated, id={}", tag, category.getId());
            CategoryResponse response = categoryMapper.toResponse(category);
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        final String tag = "CategoryServiceImpl.delete";
        log.debug("{}: Enter with params: id={}", tag, id);
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - category id={}", tag, id);
                        return new NotFoundException("Category not found");
                    });
            long deps = productRepository.countByCategory_Id(id);
            log.debug("{}: Dependent products count={}", tag, deps);
            if (deps > 0) {
                log.error("{}: Conflict - related products exist for category id={}", tag, id);
                throw new ConflictException("Cannot delete category: related products exist");
            }
            categoryRepository.delete(category);
            log.info("{}: Category deleted, id={}", tag, category.getId());
            log.debug("{}: Exit without result (void)", tag);
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
}
}