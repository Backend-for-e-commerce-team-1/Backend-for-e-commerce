package ru.practikum.masters.goodsservice.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practikum.masters.goodsservice.common.exception.BaseServiceException;
import ru.practikum.masters.goodsservice.common.exception.model.ApiError;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<ApiError> handleBase(BaseServiceException ex, HttpServletRequest request) {
        ApiError body = ApiError.of(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getDetails());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex, HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "";
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Invalid request";
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), java.util.Map.of(
                "field", field,
                "error", errorMessage
        ));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest request) {
        ApiError body = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request.getRequestURI(), Map.of());
        return ResponseEntity.internalServerError().body(body);
    }
}