package ru.practikum.masters.goodsservice.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSearchRequest extends ProductFilterRequest {
    @NotBlank(message = "query must be non-blank")
    private String query;

    @NotEmpty(message = "fields must be non-empty")
    private List<String> fields;
}