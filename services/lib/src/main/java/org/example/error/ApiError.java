package org.example.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO для представления информации об ошибке в REST API.
 * Используется глобальным обработчиком исключений.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        OffsetDateTime timestamp,
        String message,
        String path,
        List<FieldViolation> violations
) {
    /**
     * DTO для описания нарушения валидации отдельного поля.
     */
    public record FieldViolation(String field, String message) {}

    /** Ошибка без списка нарушений. */
    public static ApiError of(String message, String path) {
        return new ApiError(
                OffsetDateTime.now(),
                message,
                path,
                null
        );
    }

    /** Ошибка со списком нарушений. */
    public static ApiError of(String message, String path, List<FieldViolation> violations) {
        return new ApiError(
                OffsetDateTime.now(),
                message,
                path,
                violations
        );
    }

    /** 400 Bad Request с произвольным сообщением. */
    public static ApiError badRequest(String message, String path) {
        return of(message, path);
    }

    /** 400 Bad Request для ошибок валидации. */
    public static ApiError validationFailed(String path, List<FieldViolation> violations) {
        return of("Validation failed", path, violations);
    }

    /** 404 Not Found. */
    public static ApiError notFound(String message, String path) {
        return of(message, path);
    }

    /** 409 Conflict. */
    public static ApiError conflict(String message, String path) {
        return of(message, path);
    }

    /** 500 Internal Server Error. */
    public static ApiError internal(String path) {
        return of("Unexpected error", path);
    }
}
