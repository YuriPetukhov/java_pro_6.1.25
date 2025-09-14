package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.example.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Базовый глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Перехватывает распространённые ошибки и преобразует их в унифицированный формат {@link ApiError},
 * который возвращается клиенту с корректным HTTP-статусом.
 */
@RestControllerAdvice
public class BaseGlobalExceptionHandler {

    /**
     * 404 Not Found: сущность не найдена.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(NotFoundException ex, HttpServletRequest req) {
        return ApiError.notFound(ex.getMessage(), req.getRequestURI());
    }

    /**
     * 400 Bad Request: некорректные аргументы (бизнес-валидация, логика).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badArg(IllegalArgumentException ex, HttpServletRequest req) {
        return ApiError.badRequest(ex.getMessage(), req.getRequestURI());
    }

    /**
     * 400 Bad Request: ошибки валидации тела запроса (@Valid на @RequestBody).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError beanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ApiError.validationFailed(req.getRequestURI(), v);
    }

    /**
     * 400 Bad Request: ошибки валидации параметров запроса и путевых переменных.
     * <ul>
     *     <li>{@link BindException} — ошибки биндинга параметров</li>
     *     <li>{@link ConstraintViolationException} — нарушения @Valid в параметрах</li>
     * </ul>
     */
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

    /**
     * 400 Bad Request: ошибки синтаксиса запроса.
     * <ul>
     *     <li>{@link HttpMessageNotReadableException} — невалидный JSON</li>
     *     <li>{@link MissingServletRequestParameterException} — отсутствует обязательный параметр</li>
     *     <li>{@link MethodArgumentTypeMismatchException} — несовместимый тип параметра</li>
     * </ul>
     */
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

    /**
     * 500 Internal Server Error: непредвидённые ошибки.
     * <p>
     * Клиенту возвращается общий ответ без деталей внутренней ошибки.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError unexpected(Exception ex, HttpServletRequest req) {
        return ApiError.internal(req.getRequestURI());
    }
}
