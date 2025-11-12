package ru.practikum.masters.goodsservice.product.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.product.dto.*;
import ru.practikum.masters.goodsservice.product.service.ProductService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class ProductControllerTestConfig {
        @Bean
        ProductService productService() {
            return org.mockito.Mockito.mock(ProductService.class);
        }
    }

    @Test
    void getProducts_returns200WithList() throws Exception {
        ProductResponse item = ProductResponse.builder()
                .productId(UUID.randomUUID())
                .code("C-1")
                .name("Phone")
                .build();
        ProductListResponse.PaginationInfo pageInfo = ProductListResponse.PaginationInfo.builder()
                .currentPage(1)
                .totalPages(1)
                .totalItems(1L)
                .build();
        ProductListResponse resp = ProductListResponse.builder()
                .products(List.of(item))
                .pagination(pageInfo)
                .build();

        given(productService.getProducts(any(ProductFilterRequest.class))).willReturn(resp);

        mockMvc.perform(get("/products")
                        .param("page", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.products[0].code", is("C-1")))
                .andExpect(jsonPath("$.pagination.currentPage", is(1)))
                .andExpect(jsonPath("$.pagination.totalItems", is(1)));
    }

    @Test
    void get_returns200WithDetail() throws Exception {
        UUID id = UUID.randomUUID();
        ProductDetailResponse resp = ProductDetailResponse.builder()
                .productId(id)
                .code("C-1")
                .name("Phone")
                .build();
        given(productService.get(eq(id))).willReturn(resp);

        mockMvc.perform(get("/products/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Phone")))
                .andExpect(jsonPath("$.code", is("C-1")));
    }

    @Test
    void get_notFound_returns404ApiError() throws Exception {
        UUID id = UUID.randomUUID();
        given(productService.get(eq(id))).willThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/products/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Product not found")))
                .andExpect(jsonPath("$.path", is("/products/" + id)))
                .andExpect(jsonPath("$.details", aMapWithSize(0)));
    }

    @Test
    void search_returns200WithList() throws Exception {
        ProductListResponse resp = ProductListResponse.builder()
                .products(List.of())
                .pagination(ProductListResponse.PaginationInfo.builder()
                        .currentPage(1)
                        .totalPages(0)
                        .totalItems(0L)
                        .build())
                .build();
        given(productService.search(any(ProductSearchRequest.class))).willReturn(resp);

        mockMvc.perform(get("/products/search")
                        .param("query", "ace")
                        .param("fields", "code, name")
                        .param("offset", "0")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(0)))
                .andExpect(jsonPath("$.pagination.currentPage", is(1)))
                .andExpect(jsonPath("$.pagination.totalItems", is(0)));
    }

    @Test
    void search_missingQuery_triggersBindException_returns400ApiError() throws Exception {
        mockMvc.perform(get("/products/search")
                        // missing query
                        .param("fields", "code, name")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.path", is("/products/search")))
                .andExpect(jsonPath("$.details.field", anyOf(is("query"), is("fields"))))
                .andExpect(jsonPath("$.details.error", not(blankOrNullString())));
    }

    @Test
    void delete_returns204NoBody() throws Exception {
        UUID id = UUID.randomUUID();
        given(productService.delete(eq(id))).willReturn(ProductDeleteResponce.builder()
                .message("Product deleted successfully")
                .status("DELETED")
                .id(id)
                .build());

        mockMvc.perform(delete("/products/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}