package ru.practicum.masters.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.masters.exceptions.exception.*;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(
            final ResourceNotFoundException e,
            final HttpServletRequest request) {
        log.warn("Resource not found: {} for path: {}", e.getMessage(), request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.NOT_FOUND,
                "Resource not found",
                e.getMessage(),
                request.getRequestURI());

    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateResourceException(
            final DuplicateResourceException e,
            final HttpServletRequest request) {
        log.warn("Duplicate resource: {} for path: {}", e.getMessage(), request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.CONFLICT,
                "Resource already exists",
                e.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientStockException(
            final InsufficientStockException e,
            final HttpServletRequest request) {
        log.warn("Insufficient stock: {} for path: {}", e.getMessage(), request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.CONFLICT,
                "Insufficient quantity available",
                e.getMessage(),
                request.getRequestURI());

    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidTokenException(
            final InvalidTokenException e,
            final HttpServletRequest request) {
        log.warn("Invalid token: {} for path: {}", e.getMessage(), request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.UNAUTHORIZED,
                "Token is invalid or expired",
                e.getMessage(),
                request.getRequestURI());

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentialsException(
            final InvalidCredentialsException e,
            final HttpServletRequest request) {
        log.warn("Invalid credentials for path: {}", request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.UNAUTHORIZED,
                "Invalid username or password",
                e.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(
            AccessDeniedException e,
            HttpServletRequest request) {
        log.warn("Access denied: {} for path: {}", e.getMessage(), request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.FORBIDDEN,
                "Access denied",
                e.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e,
            final HttpServletRequest request) {

        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {} for path: {}", errorMessage, request.getRequestURI());
        return new ErrorResponse(
                ErrorStatus.BAD_REQUEST,
                "Validation failed",
                errorMessage,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(
            final Exception e,
            final HttpServletRequest request) {
        log.error("Internal Server Error for path: {}", request.getRequestURI(), e);
        return new ErrorResponse(
                ErrorStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }
}
