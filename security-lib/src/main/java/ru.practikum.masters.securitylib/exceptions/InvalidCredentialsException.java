package ru.practikum.masters.securitylib.exceptions;

public class InvalidCredentialsException extends RuntimeException {

    private final String code;
    private final String reason;

    public InvalidCredentialsException(String code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("Code '%s';  reason: %s", code, reason);
    }

}
