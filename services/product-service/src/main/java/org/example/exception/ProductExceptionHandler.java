package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.example.error.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Обработчик исключений product-service.
 * Возвращает унифицированный формат {@link ApiError}.
 */
@RestControllerAdvice
public class ProductExceptionHandler {

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

    /** Валидация тела запроса (@Valid на @RequestBody). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError beanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ApiError.validationFailed(req.getRequestURI(), v);
    }

    /** Валидация параметров запроса и path-переменных. */
    @ExceptionHandler({ BindException.class, ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError paramValidation(Exception ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v =
                (ex instanceof BindException be)
                        ? be.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                        .toList()
                        : ((ConstraintViolationException) ex).getConstraintViolations().stream()
                        .map(cv -> new ApiError.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                        .toList();
        return ApiError.validationFailed(req.getRequestURI(), v);
    }

    /** Синтаксические ошибки запроса: невалидный JSON, нет обязательного параметра, несовместимый тип. */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(Exception ex, HttpServletRequest req) {
        var msg = ex.getMessage();
        return ApiError.badRequest((msg == null || msg.isBlank()) ? ex.toString() : msg, req.getRequestURI());
    }

    /** Конфликты целостности данных (уникальные ключи, FK и т.д.). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return ApiError.conflict("Data integrity violation: " + msg, req.getRequestURI());
    }

    /** Прочие непредвидённые ошибки. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError unexpected(Exception ex, HttpServletRequest req) {
        return ApiError.internal(req.getRequestURI());
    }
}
