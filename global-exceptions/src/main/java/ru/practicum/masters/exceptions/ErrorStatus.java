package ru.practicum.masters.exceptions;

import lombok.Getter;

    @Getter
    public enum ErrorStatus {
        BAD_REQUEST("BAD_REQUEST"),
        UNAUTHORIZED("UNAUTHORIZED"),
        FORBIDDEN("FORBIDDEN"),
        NOT_FOUND("NOT_FOUND"),
        CONFLICT("CONFLICT"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

        private final String status;

        ErrorStatus(String status) {
            this.status = status;
        }

    }
