package org.example.exception;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Преобразует распространённые исключения в унифицированный формат {@link ApiError}
 * с корректными HTTP-статусами и полезными сообщениями для клиента.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Формирует тело ошибки {@link ApiError} без списка нарушений.
     *
     * @param status HTTP-статус
     * @param message сообщение об ошибке
     * @param path путь запроса (URI)
     * @return объект {@link ApiError} для сериализации в ответ
     */
    private ApiError body(HttpStatus status, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    /**
     * 404 Not Found: сущность не найдена.
     *
     * @param ex  исключение доменного уровня
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 404 и телом {@link ApiError}
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI()));
    }

    /**
     * 400 Bad Request: некорректные аргументы метода/бизнес-проверки.
     *
     * @param ex  {@link IllegalArgumentException}
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 400 и телом {@link ApiError}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI()));
    }

    /**
     * 409 Conflict: нарушение целостности данных (например, уникальные ключи).
     *
     * @param ex  {@link DataIntegrityViolationException}
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 409 и телом {@link ApiError}
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Data integrity violation: " + rootMsg(ex), req.getRequestURI()));
    }

    /**
     * 400 Bad Request: ошибки валидации тела запроса (@Valid для @RequestBody).
     *
     * @param ex  {@link MethodArgumentNotValidException}
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 400, списком нарушений и телом {@link ApiError}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        var err = new ApiError(OffsetDateTime.now(), 400, "Bad Request",
                "Validation failed", req.getRequestURI(), violations);
        return ResponseEntity.badRequest().body(err);
    }

    /**
     * 400 Bad Request: ошибки валидации параметров запроса/путевых переменных.
     *
     * @param ex  {@link ConstraintViolationException}
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 400, списком нарушений и телом {@link ApiError}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        var violations = ex.getConstraintViolations().stream()
                .map(cv -> new ApiError.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());
        var err = new ApiError(OffsetDateTime.now(), 400, "Bad Request",
                "Validation failed", req.getRequestURI(), violations);
        return ResponseEntity.badRequest().body(err);
    }

    /**
     * 400 Bad Request: некорректное тело запроса/параметры/типы.
     * <ul>
     *   <li>{@link HttpMessageNotReadableException} — невалидный JSON/тип содержимого</li>
     *   <li>{@link MissingServletRequestParameterException} — отсутствует обязательный параметр</li>
     *   <li>{@link MethodArgumentTypeMismatchException} — несовпадение типов параметров</li>
     * </ul>
     *
     * @param ex  одно из перечисленных исключений
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 400 и телом {@link ApiError}
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        return ResponseEntity.badRequest()
                .body(body(HttpStatus.BAD_REQUEST, rootMsg(ex), req.getRequestURI()));
    }

    /**
     * 500 Internal Server Error: непредвиденные ошибки.
     * <p>
     * Сообщение скрыто за общей формулировкой, чтобы не раскрывать внутренние детали.
     *
     * @param ex  любая необработанная ошибка
     * @param req HTTP-запрос для извлечения URI
     * @return ответ со статусом 500 и телом {@link ApiError}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI()));
    }

    /**
     * Извлекает максимально специфичное сообщение из цепочки причин исключения.
     *
     * @param t исходное исключение
     * @return сообщение из корневой причины или её {@code toString()}, если сообщение пустое
     */
    private static String rootMsg(Throwable t) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(t);
        String msg = root.getMessage();
        return (msg == null || msg.isBlank()) ? root.toString() : msg;
    }
}
