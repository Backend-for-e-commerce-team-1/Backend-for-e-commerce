package ru.practikum.masters.goodsservice.common.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApiError {
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, Object> details;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public static ApiError of(HttpStatus status, String message, String path, Map<String, Object> details) {
        return ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .details(details == null ? Map.of() : Map.copyOf(details))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
