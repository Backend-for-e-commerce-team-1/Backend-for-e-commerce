package ru.practikum.masters.goodsservice.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseServiceException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}