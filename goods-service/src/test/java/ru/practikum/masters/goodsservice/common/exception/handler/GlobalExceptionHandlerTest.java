package ru.practikum.masters.goodsservice.common.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;
import ru.practikum.masters.goodsservice.common.exception.model.ApiError;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBase_returnsApiErrorWithStatusAndDetails() {
        ValidationException ex = new ValidationException("Invalid", Map.of("field", "code"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/path");

        ResponseEntity<ApiError> response = handler.handleBase(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Invalid");
        assertThat(body.getPath()).isEqualTo("/test/path");
        assertThat(body.getDetails()).containsEntry("field", "code");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleBindException_returnsApiErrorWithFieldAndMessage() {
        Object target = new Object();
        BindException bindEx = new BindException(target, "request");
        bindEx.getBindingResult().addError(new FieldError("request", "query", "must not be blank"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/products/search");

        ResponseEntity<ApiError> response = handler.handleBindException(bindEx, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Validation failed");
        assertThat(body.getPath()).isEqualTo("/products/search");
        assertThat(body.getDetails()).containsEntry("field", "query");
        assertThat(body.getDetails()).containsEntry("error", "must not be blank");
    }
}