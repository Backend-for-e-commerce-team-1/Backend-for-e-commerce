package ru.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class TestControllerForTracing {

    @GetMapping("/test")
    public Map<String, String> test() {
        log.info("🔧 Test endpoint called - checking basic functionality");
        return Map.of("status", "OK", "message", "Tracing test");
    }

    @GetMapping("/goods")
    public List<String> getGoods() {
        log.info("📦 Getting mock goods list");
        log.debug("Debug message with trace ID");
        return List.of("Product 1", "Product 2", "Product 3");
    }

    @GetMapping("/goods/{id}")
    public Map<String, Object> getGoodById(@PathVariable Long id) {
        log.info("🔍 Getting good by ID: {}", id);

        if (id > 100) {
            log.warn("⚠️ Requested ID is too large: {}", id);
        }

        return Map.of(
                "id", id,
                "name", "Mock Product " + id,
                "price", 99.99
        );
    }

    @PostMapping("/goods")
    public Map<String, Object> createGood(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        log.info("🆕 Creating new good: {}", name);

        return Map.of(
                "id", 123,
                "name", name,
                "status", "created"
        );
    }
}