package org.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO для представления информации об ошибке в REST API.
 * Используется глобальным обработчиком исключений.
 *
 * @param timestamp  время возникновения ошибки
 * @param status     HTTP-статус ответа
 * @param error      краткое название ошибки (например, "Bad Request")
 * @param message    подробное сообщение об ошибке
 * @param path       URL-адрес запроса, в котором произошла ошибка
 * @param violations список нарушений валидации (может быть {@code null})
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
) {

    /**
     * DTO для описания нарушения валидации отдельного поля.
     *
     * @param field   имя поля, в котором обнаружена ошибка
     * @param message сообщение об ошибке
     */
    public record FieldViolation(String field, String message) {}
}
