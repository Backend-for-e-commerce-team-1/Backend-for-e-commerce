package ru.practikum.masters.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.practicum.masters.exceptions.GlobalExceptionHandler;
import ru.practicum.masters.exceptions.ErrorResponse;
import ru.practicum.masters.exceptions.config.GlobalExceptionsProperties;
import ru.practicum.masters.exceptions.exception.DuplicateResourceException;
import ru.practicum.masters.exceptions.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ExceptionTest.TestConfig.class)
public class ExceptionTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public GlobalExceptionsProperties globalExceptionsProperties() {
            return new GlobalExceptionsProperties();
        }

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler(globalExceptionsProperties());
        }
    }

    @Test
    void testResourceNotFoundExceptionHandling() {
        // given
        ResourceNotFoundException exception = new ResourceNotFoundException("Ресурс не найден");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/someUrl");

        // when
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new GlobalExceptionsProperties());
        ErrorResponse response = handler.handleResourceNotFoundException(exception, request);

        // then
        assertNotNull(response);
        assertEquals("NOT_FOUND", response.getStatus());
        assertEquals("Resource not found", response.getError());
        assertEquals("Ресурс не найден", response.getMessage());
        assertEquals("/someUrl", response.getPath());

        System.out.println("ResourceNotFoundException обрабатывается корректно!");
    }

    @Test
    void testDuplicateResourceExceptionHandling() {
        // given
        DuplicateResourceException exception = new DuplicateResourceException("Дубликат ресурса");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/someUrl");

        // when
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new GlobalExceptionsProperties());
        ErrorResponse response = handler.handleDuplicateResourceException(exception, request);

        // then
        assertNotNull(response);
        assertEquals("CONFLICT", response.getStatus());
        assertEquals("Resource already exists", response.getError());
        assertEquals("Дубликат ресурса", response.getMessage());
        assertEquals("/someUrl", response.getPath());

        System.out.println("DuplicateResourceException обрабатывается корректно!");
    }
}