package ru.practikum.masters.goodsservice.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practikum.masters.goodsservice.brand.model.Brand;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Brand> findByNameIgnoreCase(String name);
}