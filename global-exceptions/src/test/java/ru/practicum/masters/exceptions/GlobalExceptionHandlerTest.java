package ru.practicum.masters.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.masters.exceptions.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldHandleResourceNotFoundException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        ResourceNotFoundException exception = new ResourceNotFoundException("Not Found");

        ErrorResponse response = exceptionHandler.handleResourceNotFoundException(exception, request);
        assertEquals("NOT_FOUND", response.getStatus());
        assertEquals("Resource not found", response.getError());
        assertEquals("Not Found", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleInvalidTokenException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        InvalidTokenException exception = new InvalidTokenException("Invalid Token Exception");

        ErrorResponse response = exceptionHandler.handleInvalidTokenException(exception, request);
        assertEquals("UNAUTHORIZED", response.getStatus());
        assertEquals("Token is invalid or expired", response.getError());
        assertEquals("Invalid Token Exception", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid Credentials Exception");
        ErrorResponse response = exceptionHandler.handleInvalidCredentialsException(exception, request);
        assertEquals("UNAUTHORIZED", response.getStatus());
        assertEquals("Invalid username or password", response.getError());
        assertEquals("Invalid Credentials Exception", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleDuplicateResourceException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        DuplicateResourceException exception = new DuplicateResourceException("Duplicate Resource Exception");
        ErrorResponse response = exceptionHandler.handleDuplicateResourceException(exception, request);
        assertEquals("CONFLICT", response.getStatus());
        assertEquals("Resource already exists", response.getError());
        assertEquals("Duplicate Resource Exception", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleInsufficientStockException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        InsufficientStockException exception = new InsufficientStockException("Insufficient Stock Exception");
        ErrorResponse response = exceptionHandler.handleInsufficientStockException(exception, request);
        assertEquals("CONFLICT", response.getStatus());
        assertEquals("Insufficient quantity available", response.getError());
        assertEquals("Insufficient Stock Exception", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        when(request.getRequestURI()).thenReturn("/somePath");
        AccessDeniedException exception = new AccessDeniedException("Access Denied Exception");
        ErrorResponse response = exceptionHandler.handleAccessDeniedException(exception, request);
        assertEquals("FORBIDDEN", response.getStatus());
        assertEquals("Access denied", response.getError());
        assertEquals("Access Denied Exception", response.getMessage());
        assertEquals("/somePath", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleInternalServerError() {
        when(request.getRequestURI()).thenReturn("/somePath");
        Exception exception = new RuntimeException("Unexpected error");

        ErrorResponse response = exceptionHandler.handleInternalServerError(exception, request);

        assertEquals("INTERNAL_SERVER_ERROR", response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("An unexpected error occurred", response.getMessage());
        assertEquals("/somePath", response.getPath());
    }

}