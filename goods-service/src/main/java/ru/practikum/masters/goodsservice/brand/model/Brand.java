package ru.practikum.masters.goodsservice.brand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "brands")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {
    @Id
    @UuidGenerator
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String name;

    public static Brand createWithName(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return brand;
    }
}
