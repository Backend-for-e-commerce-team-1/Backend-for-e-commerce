package ru.practikum.masters.goodsservice.brand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.service.BrandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Controller", description = "Управление брендами товаров")
public class BrandController {
    private final BrandService brandService;


    @Operation(
            summary = "Создание нового бренда",
            description = "Создает новый бренд в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Бренд успешно создан",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Бренд с таким названием уже существует"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(@Valid @RequestBody BrandRequest request) {
        return brandService.create(request);
    }


    @Operation(
            summary = "Получение списка брендов",
            description = "Возвращает список брендов с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список брендов успешно получен"
            )
    })
    @GetMapping
    public Page<BrandResponse> list(Pageable pageable) {
        return brandService.list(pageable);
    }

    @Operation(
            summary = "Получение бренда по ID",
            description = "Возвращает информацию о конкретном бренде"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Бренд найден",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Бренд не найден"
            )
    })
    @GetMapping("/{id}")
    public BrandResponse get(@PathVariable UUID id) {
        return brandService.get(id);
    }

    @Operation(
            summary = "Обновление бренда",
            description = "Обновляет информацию о существующем бренде"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Бренд успешно обновлен",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Бренд не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Бренд с таким названием уже существует"
            )
    })
    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) {
        return brandService.update(id, request);
    }

    @Operation(
            summary = "Удаление бренда",
            description = "Удаляет бренд из системы по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Бренд успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Бренд не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Нельзя удалить бренд, так как существуют связанные товары"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        brandService.delete(id);
    }
}