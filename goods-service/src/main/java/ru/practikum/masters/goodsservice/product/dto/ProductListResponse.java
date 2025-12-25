package ru.practikum.masters.goodsservice.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ со списком товаров и информацией о пагинации")
public class ProductListResponse {

    @Schema(
            description = "Список товаров",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ProductResponse> products;

    @Schema(
            description = "Информация о пагинации",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Информация о пагинации")
    public static class PaginationInfo {

        @Schema(
                description = "Текущая страница",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private Integer currentPage;

        @Schema(
                description = "Общее количество страниц",
                example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private Integer totalPages;

        @Schema(
                description = "Общее количество товаров",
                example = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private Long totalItems;
    }
}