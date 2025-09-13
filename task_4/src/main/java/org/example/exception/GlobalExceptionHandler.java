package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Преобразует типовые ошибки в унифицированный формат {@link ApiError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 Not Found: сущность не найдена. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ApiError.notFound(ex.getMessage(), req.getRequestURI());
    }

    /** 400 Bad Request: некорректные аргументы/бизнес-проверки. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ApiError.badRequest(ex.getMessage(), req.getRequestURI());
    }

    /** 409 Conflict: нарушение целостности данных (уникальность и пр.). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ApiError.conflict("Data integrity violation: " + rootMsg(ex), req.getRequestURI());
    }

    /** 400 Bad Request: ошибки валидации тела запроса (@Valid для @RequestBody). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ApiError.validationFailed(req.getRequestURI(), violations);
    }

    /** 400 Bad Request: ошибки валидации параметров запроса/путевых переменных. */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new ApiError.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        return ApiError.validationFailed(req.getRequestURI(), violations);
    }

    /**
     * 400 Bad Request: невалидный JSON/контент, отсутствующие параметры,
     * несовпадение типов параметров и т. п.
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(Exception ex, HttpServletRequest req) {
        return ApiError.badRequest(rootMsg(ex), req.getRequestURI());
    }

    /** 500 Internal Server Error: непредвидённые ошибки. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception ex, HttpServletRequest req) {
        return ApiError.internal(req.getRequestURI());
    }

    /** Достаёт максимально специфичное сообщение из цепочки причин исключения. */
    private static String rootMsg(Throwable t) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(t);
        String msg = root.getMessage();
        return (msg == null || msg.isBlank()) ? root.toString() : msg;
    }
}
