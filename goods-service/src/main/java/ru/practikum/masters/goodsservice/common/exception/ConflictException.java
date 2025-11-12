package ru.practikum.masters.goodsservice.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseServiceException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}