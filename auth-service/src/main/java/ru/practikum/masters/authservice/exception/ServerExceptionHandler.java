package ru.practikum.masters.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ServerExceptionHandler {

    /**
     * Группа: Ошибки "Не найдено"
     *
     * @param ex RuntimeException
     * @return ErrorResponse
     */
    @ExceptionHandler({
            NotFoundException.class,
            RequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(RuntimeException ex) {
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                getNotFoundMessage(ex), LocalDateTime.now());
    }

    /**
     * Группа: Конфликты данных и ограничения БД
     *
     * @param ex RuntimeException
     * @return ErrorResponse
     */
    @ExceptionHandler({
            DataConflictException.class,
            ConstraintViolationException.class,
            DuplicateUserException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictExceptions(RuntimeException ex) {
        String details = ex instanceof DataConflictException ?
                "could not execute statement; " + ex.getMessage() :
                "could not execute statement; ";
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.",
                details, LocalDateTime.now());
    }

    /**
     * Группа: Ошибки валидации
     *
     * @param ex Exception
     * @return ErrorResponse
     */
    @ExceptionHandler({
            ParameterNotValidException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception ex) {
        String message = ex instanceof ParameterNotValidException ?
                ex.getMessage() :
                "Validation failed for argument.";
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.",
                message, LocalDateTime.now());
    }

    /**
     * Группа: Ошибки аутентификации и авторизации
     *
     * @param ex RuntimeException
     * @return ErrorResponse
     */
    @ExceptionHandler({
            AuthenticationException.class,
            JwtValidationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthExceptions(RuntimeException ex) {
        String details = ex instanceof JwtValidationException ?
                "JwtValidationException: " + ex.getMessage() :
                "The client did not provide valid credentials or they were incorrect: " + ex.getMessage();
        return new ErrorResponse("UNAUTHORIZED", "Unauthorized", details, LocalDateTime.now());
    }

    /**
     * Вспомогательный метод для формирования сообщений "Не найдено"
     *
     * @param ex RuntimeException
     * @return String
     */
    private String getNotFoundMessage(RuntimeException ex) {
        if (ex instanceof RequestNotFoundException) {
            return "Запрос не найден; " + ex.getMessage();
        }
        return "Ресурс не найден; " + ex.getMessage();
    }
}

