package ru.practikum.masters.goodsservice.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.product.dto.ProductRequest;
import ru.practikum.masters.goodsservice.product.dto.ProductCreateResponse;
import ru.practikum.masters.goodsservice.product.service.ProductService;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class AdminProductControllerTestConfig {
        @Bean
        ProductService productService() {
            return org.mockito.Mockito.mock(ProductService.class);
        }
    }

    @Test
    void create_returns201WithResponse() throws Exception {
        UUID id = UUID.randomUUID();
        ProductCreateResponse resp = ProductCreateResponse.builder()
                .productId(id)
                .message("Product created successfully.")
                .build();
        given(productService.create(any(ProductRequest.class))).willReturn(resp);

        ProductRequest req = ProductRequest.builder()
                .name("Phone")
                .code("C-1")
                .description("Desc")
                .price(java.math.BigDecimal.valueOf(1000))
                .categoryId(UUID.randomUUID())
                .brandId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(id.toString())))
                .andExpect(jsonPath("$.message", is("Product created successfully.")));
    }

    @Test
    void create_categoryNotFound_returns404ApiError() throws Exception {
        given(productService.create(any(ProductRequest.class))).willThrow(new NotFoundException("Category not found"));

        ProductRequest req = ProductRequest.builder()
                .name("Phone")
                .code("C-1")
                .description("Desc")
                .price(java.math.BigDecimal.valueOf(1000))
                .categoryId(UUID.randomUUID())
                .brandId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Category not found")))
                .andExpect(jsonPath("$.path", is("/admin/products")));
    }
}