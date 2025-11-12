package ru.practikum.masters.goodsservice.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.brand.repository.BrandRepository;
import ru.practikum.masters.goodsservice.category.model.Category;
import ru.practikum.masters.goodsservice.category.repository.CategoryRepository;
import ru.practikum.masters.goodsservice.product.configuration.pagination.PageableFactory;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.mapper.ProductMapper;
import ru.practikum.masters.goodsservice.product.model.Product;
import ru.practikum.masters.goodsservice.product.repository.ProductRepository;
import ru.practikum.masters.goodsservice.product.spec.ProductSearchSpecification;
import ru.practikum.masters.goodsservice.product.spec.ProductSpecification;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSpecification specification;
    private final ProductSearchSpecification productSearchSpecification;
    private final PageableFactory pageableFactory;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public ProductCreateResponse create(ProductRequest productRequest) {
        final String tag = "ProductServiceImpl.create";
        log.debug("{}: Enter with params: productRequest={}", tag, productRequest);
        try {
            Category category;
            if (productRequest.getCategoryId() != null) {
                category = categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(() -> {
                            log.error("{}: NotFound - category id={}", tag, productRequest.getCategoryId());
                            return new NotFoundException("Category not found");
                        });
            } else if (productRequest.getCategoryName() != null && !productRequest.getCategoryName().isBlank()) {
                category = categoryRepository.findByNameIgnoreCase(productRequest.getCategoryName())
                        .orElseThrow(() -> {
                            log.error("{}: NotFound - category name={}", tag, productRequest.getCategoryName());
                            return new NotFoundException("Category not found");
                        });
            } else {
                throw new ValidationException("Category is required by id or name");
            }

            Brand brand;
            if (productRequest.getBrandId() != null) {
                brand = brandRepository.findById(productRequest.getBrandId())
                        .orElseThrow(() -> {
                            log.error("{}: NotFound - brand id={}", tag, productRequest.getBrandId());
                            return new NotFoundException("Brand not found");
                        });
            } else if (productRequest.getBrandName() != null && !productRequest.getBrandName().isBlank()) {
                brand = brandRepository.findByNameIgnoreCase(productRequest.getBrandName())
                        .orElseThrow(() -> {
                            log.error("{}: NotFound - brand name={}", tag, productRequest.getBrandName());
                            return new NotFoundException("Brand not found");
                        });
            } else {
                throw new ValidationException("Brand is required by id or name");
            }

            Product product = productMapper.toEntity(productRequest, category, brand);
            log.debug("{}: Mapped entity: {}", tag, product);
            product = productRepository.save(product);
            log.info("{}: Successfully created product, id={}", tag, product.getId());

            ProductCreateResponse response = productMapper.toShortResponse(product.getId(), "Product created successfully.");
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public ProductListResponse getProducts(ProductFilterRequest filterRequest) {
        final String tag = "ProductServiceImpl.getProducts";
        log.debug("{}: Enter with params: filterRequest={}", tag, filterRequest);
        try {
            Pageable pageable = pageableFactory.createFromFilter(filterRequest);
            log.debug("{}: Resolved pageable: pageNumber={}, pageSize={}", tag, pageable.getPageNumber(), pageable.getPageSize());
            Specification<Product> spec = specification.buildFromFilter(filterRequest);
            Page<Product> productPage = productRepository.findAll(spec, pageable);
            log.info("{}: Fetched products page, totalElements={}", tag, productPage.getTotalElements());

            ProductListResponse response = productMapper.toProductListResponse(productPage, filterRequest);
            log.debug("{}: Exit with result page size={}", tag, productPage.getContent().size());
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public ProductDetailResponse get(UUID id) {
        final String tag = "ProductServiceImpl.get";
        log.debug("{}: Enter with params: id={}", tag, id);
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - product id={}", tag, id);
                        return new NotFoundException("Product not found");
                    });
            ProductDetailResponse response = productMapper.toDetailResponse(product);
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    public ProductListResponse search(ProductSearchRequest request) {
        final String tag = "ProductServiceImpl.search";
        log.debug("{}: Enter with params: request={}", tag, request);
        try {
            List<String> fields = request.getFields();
            String query = request.getQuery();
            if (fields == null || fields.isEmpty()) {
                throw new ValidationException("fields parameter is required and must be non-empty");
            }
            java.util.Set<String> allowed = java.util.Set.of("code", "brand", "name");
            java.util.Set<String> fieldSet;
            if (fields.size() == 1 && fields.get(0) != null && fields.get(0).contains(",")) {
                fieldSet = java.util.Arrays.stream(fields.get(0).split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toSet());
            } else {
                fieldSet = new java.util.HashSet<>(fields);
            }
            fieldSet = fieldSet.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());
            if (!allowed.containsAll(fieldSet)) {
                throw new ValidationException("fields must be subset of: code, brand, name");
            }
            Pageable pageable = pageableFactory.createFromFilter(request);
            log.debug("{}: Resolved pageable: pageNumber={}, pageSize={}", tag, pageable.getPageNumber(), pageable.getPageSize());
            Specification<Product> spec = productSearchSpecification.searchByFields(query, fieldSet)
                    .and(specification.buildFromFilter(request));
            Page<Product> productPage = productRepository.findAll(spec, pageable);
            log.info("{}: Fetched search products page, totalElements={}", tag, productPage.getTotalElements());
            ProductListResponse response = productMapper.toProductListResponse(productPage, request);
            log.debug("{}: Exit with result page size={}", tag, productPage.getContent().size());
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductDeleteResponce delete(UUID id) {
        final String tag = "ProductServiceImpl.delete";
        log.debug("{}: Enter with params: id={}", tag, id);
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - product id={}", tag, id);
                        return new NotFoundException("Product not found");
                    });
            productRepository.delete(product);
            log.info("{}: Product deleted, id={}", tag, product.getId());
            ProductDeleteResponce response = ProductDeleteResponce.builder()
                    .message("Product deleted successfully")
                    .status("DELETED")
                    .id(product.getId())
                    .build();
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
        } catch (Exception e) {
            log.error("{}: Error - {}", tag, e.getMessage());
            throw e;
        }
    }
}
