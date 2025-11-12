package ru.practikum.masters.goodsservice.brand.controller;

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
import ru.practikum.masters.goodsservice.brand.dto.BrandRequest;
import ru.practikum.masters.goodsservice.brand.dto.BrandResponse;
import ru.practikum.masters.goodsservice.brand.service.BrandService;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandService brandService;

    @TestConfiguration
    static class BrandControllerTestConfig {
        @Bean
        BrandService brandService() {
            return org.mockito.Mockito.mock(BrandService.class);
        }
    }

    @Test
    void create_returns201WithResponse() throws Exception {
        UUID id = UUID.randomUUID();
        BrandResponse resp = BrandResponse.builder()
                .id(id)
                .name("Apple")
                .build();
        given(brandService.create(any(BrandRequest.class))).willReturn(resp);

        BrandRequest req = BrandRequest.builder()
                .name("Apple")
                .build();

        mockMvc.perform(post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Apple")));
    }

    @Test
    void create_conflict_returns409ApiError() throws Exception {
        given(brandService.create(any(BrandRequest.class))).willThrow(new ConflictException("Brand with the same name already exists"));

        BrandRequest req = BrandRequest.builder()
                .name("Apple")
                .build();

        mockMvc.perform(post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Brand with the same name already exists")))
                .andExpect(jsonPath("$.path", is("/brands")));
    }

    @Test
    void get_notFound_returns404ApiError() throws Exception {
        UUID id = UUID.randomUUID();
        given(brandService.get(id)).willThrow(new NotFoundException("Brand not found"));

        mockMvc.perform(get("/brands/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Brand not found")))
                .andExpect(jsonPath("$.path", is("/brands/" + id)));
    }

    @Test
    void delete_conflict_returns409ApiError() throws Exception {
        UUID id = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new ConflictException("Cannot delete brand: related products exist"))
                .when(brandService).delete(id);

        mockMvc.perform(delete("/brands/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Cannot delete brand: related products exist")))
                .andExpect(jsonPath("$.path", is("/brands/" + id)));
    }
}