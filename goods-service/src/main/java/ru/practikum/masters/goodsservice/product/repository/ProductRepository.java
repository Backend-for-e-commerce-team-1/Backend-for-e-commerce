package ru.practikum.masters.goodsservice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practikum.masters.goodsservice.brand.model.Brand;
import ru.practikum.masters.goodsservice.product.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    long countByCategory_Id(UUID categoryId);

    long countByBrand_Id(UUID brandId);

    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByBrand(Brand brand);

    Optional<Product> findByCode(String code);
}
