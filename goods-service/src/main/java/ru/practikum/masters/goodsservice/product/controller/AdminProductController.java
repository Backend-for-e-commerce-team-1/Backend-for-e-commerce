package ru.practikum.masters.goodsservice.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.product.dto.ProductRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductCreateResponse;
import ru.practikum.masters.goodsservice.product.service.ProductService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Product Controller", description = "Административное управление товарами")
public class AdminProductController {
    private final ProductService productService;

    @Operation(
            summary = "Создание нового товара",
            description = "Создает новый товар в системе. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Товар успешно создан",
                    content = @Content(schema = @Schema(implementation = ProductCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
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
                    description = "Категория или бренд не найдены"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Товар с таким названием уже существует"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCreateResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
