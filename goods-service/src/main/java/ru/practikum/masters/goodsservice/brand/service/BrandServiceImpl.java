package ru.practikum.masters.goodsservice.brand.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.mapper.BrandMapper;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.brand.repository.BrandRepository;
import ru.practikum.masters.goodsservice.common.exception.ConflictException;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.product.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public BrandResponse create(BrandRequest request) {
        final String tag = "BrandServiceImpl.create";
        log.debug("{}: Enter with params: request={}", tag, request);
            if (brandRepository.existsByNameIgnoreCase(request.getName())) {
                log.error("{}: Conflict - brand name exists: {}", tag, request.getName());
                throw new ConflictException("Brand with the same name already exists");
            }
            Brand brand = brandMapper.toEntity(request);
            log.debug("{}: Mapped entity: {}", tag, brand);
            brand = brandRepository.save(brand);
            log.info("{}: Successfully created brand, id={}", tag, brand.getId());
            BrandResponse response = brandMapper.toResponse(brand);
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
    }

    @Override
    public Page<BrandResponse> list(Pageable pageable) {
        final String tag = "BrandServiceImpl.list";
        log.debug("{}: Enter with params: pageable={}", tag, pageable);
            Page<BrandResponse> page = brandRepository.findAll(pageable).map(brandMapper::toResponse);
            log.info("{}: Fetched brands page, totalElements={}", tag, page.getTotalElements());
            log.debug("{}: Exit with result page size={}", tag, page.getContent().size());
            return page;
    }

    @Override
    public BrandResponse get(UUID id) {
        final String tag = "BrandServiceImpl.get";
        log.debug("{}: Enter with params: id={}", tag, id);
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - brand id={}", tag, id);
                        return new NotFoundException("Brand not found");
                    });
            BrandResponse response = brandMapper.toResponse(brand);
            log.info("{}: Brand fetched, id={}", tag, brand.getId());
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
    }

    @Override
    @Transactional
    public BrandResponse update(UUID id, BrandRequest request) {
        final String tag = "BrandServiceImpl.update";
        log.debug("{}: Enter with params: id={}, request={}", tag, id, request);
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - brand id={}", tag, id);
                        return new NotFoundException("Brand not found");
                    });
            brand.setName(request.getName());
            log.debug("{}: Updated brand name to: {}", tag, request.getName());
            brand = brandRepository.save(brand);
            log.info("{}: Brand updated, id={}", tag, brand.getId());
            BrandResponse response = brandMapper.toResponse(brand);
            log.debug("{}: Exit with result: {}", tag, response);
            return response;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        final String tag = "BrandServiceImpl.delete";
        log.debug("{}: Enter with params: id={}", tag, id);
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("{}: NotFound - brand id={}", tag, id);
                        return new NotFoundException("Brand not found");
                    });
            long deps = productRepository.countByBrand_Id(id);
            log.debug("{}: Dependent products count={}", tag, deps);
            if (deps > 0) {
                log.error("{}: Conflict - related products exist for brand id={}", tag, id);
                throw new ConflictException("Cannot delete brand: related products exist");
            }
            brandRepository.delete(brand);
            log.info("{}: Brand deleted, id={}", tag, brand.getId());
            log.debug("{}: Exit without result (void)", tag);
}
}