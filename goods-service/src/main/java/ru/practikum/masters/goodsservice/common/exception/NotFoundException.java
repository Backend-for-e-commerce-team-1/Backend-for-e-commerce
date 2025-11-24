package ru.practikum.masters.goodsservice.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseServiceException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}