package ru.practicum.masters.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final String status;
    private final String error;
    private final String message;
    private final String path;

    public ErrorResponse(ErrorStatus status, String error, String message, String path) {
        this.status = status.getStatus();
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(ErrorStatus status, String reason, String message) {
        this(status, reason, message, null);
    }
}

