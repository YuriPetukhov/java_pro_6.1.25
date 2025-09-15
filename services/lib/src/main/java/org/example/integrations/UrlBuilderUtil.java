package org.example.integrations;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Утилитарный класс для построения URL с параметрами.
 * <p>
 * Используется REST-клиентами для формирования запросов с пагинацией и сортировкой.
 */
public final class UrlBuilderUtil {

    private UrlBuilderUtil() {}

    /**
     * Формирует URL для запросов с пагинацией и сортировкой.
     *
     * @param baseUrl базовый URL (например, http://localhost:8081/api/v1)
     * @param path    относительный путь ресурса (например, /products)
     * @param userId  идентификатор пользователя (обязательный параметр)
     * @param page    номер страницы (0-based), может быть null
     * @param size    размер страницы, может быть null
     * @param sort    строка сортировки в формате "поле,asc|desc", может быть null
     * @return строка с готовым URL и параметрами
     */
    public static String buildPagedUrl(
            String baseUrl, String path,
            Long userId, Integer page, Integer size, String sort) {

        return UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                .queryParam("userId", userId)
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("size", Optional.ofNullable(size))
                .queryParamIfPresent("sort", Optional.ofNullable(sort))
                .toUriString();
    }
}
