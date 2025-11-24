package ru.practikum.masters.goodsservice.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practikum.masters.goodsservice.common.exception.ConflictException;
import ru.practikum.masters.goodsservice.common.exception.NotFoundException;
import ru.practikum.masters.goodsservice.category.dto.CategoryRequest;
import ru.practikum.masters.goodsservice.category.dto.CategoryResponse;
import ru.practikum.masters.goodsservice.category.service.CategoryService;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @TestConfiguration
    static class CategoryControllerTestConfig {
        @Bean
        CategoryService categoryService() {
            return org.mockito.Mockito.mock(CategoryService.class);
        }
    }

    @Test
    void create_returns201WithResponse() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryResponse resp = CategoryResponse.builder()
                .id(id)
                .name("Electronics")
                .build();
        given(categoryService.create(any(CategoryRequest.class))).willReturn(resp);

        CategoryRequest req = CategoryRequest.builder()
                .name("Electronics")
                .build();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Electronics")));
    }

    @Test
    void create_conflict_returns409ApiError() throws Exception {
        given(categoryService.create(any(CategoryRequest.class))).willThrow(new ConflictException("Category with the same name already exists"));

        CategoryRequest req = CategoryRequest.builder()
                .name("Electronics")
                .build();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Category with the same name already exists")))
                .andExpect(jsonPath("$.path", is("/categories")));
    }

    @Test
    void get_notFound_returns404ApiError() throws Exception {
        UUID id = UUID.randomUUID();
        given(categoryService.get(id)).willThrow(new NotFoundException("Category not found"));

        mockMvc.perform(get("/categories/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Category not found")))
                .andExpect(jsonPath("$.path", is("/categories/" + id)));
    }

    @Test
    void delete_conflict_returns409ApiError() throws Exception {
        UUID id = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new ConflictException("Cannot delete category: related products exist"))
                .when(categoryService).delete(id);

        mockMvc.perform(delete("/categories/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Cannot delete category: related products exist")))
                .andExpect(jsonPath("$.path", is("/categories/" + id)));
    }
}