package ru.practikum.masters.goodsservice.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductSearchRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductListResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductDetailResponse;
import ru.practikum.masters.goodsservice.product.dto.ProductDeleteResponse;
import ru.practikum.masters.goodsservice.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Управление товарами")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "Получение списка товаров с фильтрацией",
            description = "Возвращает список товаров с возможностью фильтрации по категориям, брендам и другим параметрам"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список товаров успешно получен",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные параметры фильтрации"
            )
    })
    @GetMapping
    public ProductListResponse getProducts(@Valid @ModelAttribute ProductFilterRequest filterRequest) {
        return productService.getProducts(filterRequest);
    }

    @Operation(
            summary = "Получение детальной информации о товаре",
            description = "Возвращает полную информацию о конкретном товаре по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден"
            )
    })
    @GetMapping("/{id}")
    public ProductDetailResponse get(@PathVariable UUID id) {
        return productService.get(id);
    }


    @Operation(
            summary = "Поиск товаров",
            description = "Выполняет поиск товаров"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результаты поиска успешно получены",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные параметры поиска"
            )
    })
    @GetMapping("/search")
    public ProductListResponse search(@Valid @ModelAttribute ProductSearchRequest request) {
        return productService.search(request);
    }

    @Operation(
            summary = "Удаление товара",
            description = "Удаляет товар из системы по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Товар успешно удален",
                    content = @Content(schema = @Schema(implementation = ProductDeleteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен (требуются права администратора)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Нельзя удалить товар, так как он присутствует в заказах"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProductDeleteResponse delete(@PathVariable UUID id) {
        return productService.delete(id);
    }
}
