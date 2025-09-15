package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Обработчик исключений payment-service.
 * Содержит общие правила и примеры доменных ошибок платежей.
 */
@RestControllerAdvice
public class PaymentExceptionHandler {

    /** Сущность не найдена. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(NotFoundException ex, HttpServletRequest req) {
        return ApiError.notFound(ex.getMessage(), req.getRequestURI());
    }

    /** Некорректные аргументы бизнес-логики. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badArg(IllegalArgumentException ex, HttpServletRequest req) {
        return ApiError.badRequest(ex.getMessage(), req.getRequestURI());
    }

    /** Синтаксические ошибки запроса. */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestSyntax(Exception ex, HttpServletRequest req) {
        var msg = ex.getMessage();
        return ApiError.badRequest((msg == null || msg.isBlank()) ? ex.toString() : msg, req.getRequestURI());
    }

    /** Прочие непредвидённые ошибки. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError unexpected(Exception ex, HttpServletRequest req) {
        return ApiError.internal(req.getRequestURI());
    }
}
