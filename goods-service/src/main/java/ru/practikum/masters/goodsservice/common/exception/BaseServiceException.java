package ru.practikum.masters.goodsservice.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class BaseServiceException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, Object> details;

    protected BaseServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.details = Map.of();
    }

    protected BaseServiceException(HttpStatus status, String message, Map<String, Object> details) {
        super(message);
        this.status = status;

        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

}