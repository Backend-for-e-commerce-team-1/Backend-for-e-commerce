package ru.practikum.masters.goodsservice.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseServiceException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}