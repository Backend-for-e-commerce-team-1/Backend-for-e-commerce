package ru.practikum.masters.goodsservice.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ValidationException extends BaseServiceException {
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ValidationException(String message, Map<String, Object> details) {
        super(HttpStatus.BAD_REQUEST, message, details);
    }
}