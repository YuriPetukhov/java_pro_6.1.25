package org.example.dto;

import java.util.List;

/**
 * Унифицированный DTO ответа с постраничными данными.
 * <p>
 *
 * @param <T> тип элементов в списке
 * @param content список элементов текущей страницы
 * @param page номер страницы (отсчёт с 0)
 * @param size размер страницы (число элементов на странице)
 * @param totalElements общее количество элементов во всём наборе
 * @param totalPages общее количество страниц
 * @param first признак, что это первая страница
 * @param last признак, что это последняя страница
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) { }
