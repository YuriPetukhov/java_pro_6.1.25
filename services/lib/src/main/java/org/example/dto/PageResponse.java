package org.example.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Унифицированный DTO для постраничного ответа REST API.
 * <p>
 * Используется вместо {@link org.springframework.data.domain.Page},
 * так как сериализация {@code PageImpl} даёт нестабильную структуру JSON.
 * <br>
 * Этот класс предоставляет стабильный контракт для клиентов (в данном случае, платежного сервиса).
 *
 * @param content       содержимое текущей страницы
 * @param page          номер текущей страницы (0-based)
 * @param size          размер страницы (количество элементов на странице)
 * @param totalElements общее количество элементов
 * @param totalPages    общее количество страниц
 * @param first         признак первой страницы
 * @param last          признак последней страницы
 * @param <T>           тип элементов содержимого
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    /**
     * Преобразует {@link Page} из Spring Data в стабильный {@link PageResponse}.
     *
     * @param p объект {@code Page<T>} из Spring Data
     * @param <T> тип элементов
     * @return новый {@code PageResponse<T>} с извлечёнными данными
     */
    public static <T> PageResponse<T> of(Page<T> p) {
        return new PageResponse<>(
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.isFirst(),
                p.isLast()
        );
    }
}
