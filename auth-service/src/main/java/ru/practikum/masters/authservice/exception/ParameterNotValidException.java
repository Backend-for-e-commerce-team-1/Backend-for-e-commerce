package ru.practikum.masters.authservice.exception;

public class ParameterNotValidException extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    public ParameterNotValidException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("Parameter '%s' is not valid: %s", parameter, reason);
    }

}
