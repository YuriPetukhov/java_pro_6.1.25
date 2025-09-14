package org.example.integrations;

import org.example.error.ApiError;
import org.example.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Базовый класс для REST-клиентов.
 * <p>
 * Инкапсулирует работу с {@link RestTemplate}, обрабатывает исключения
 * и преобразует ошибки удалённых сервисов в доменные исключения:
 * <ul>
 *     <li>{@link NotFoundException} — для 404 Not Found</li>
 *     <li>{@link IllegalArgumentException} — для остальных кодов ошибок</li>
 * </ul>
 * Также умеет читать {@link ApiError} из тела ответа, если сервис возвращает
 * ошибки в унифицированном формате.
 */
public abstract class RestClientSupport {

    /** Экземпляр {@link RestTemplate}, через который выполняются HTTP-запросы. */
    protected final RestTemplate rest;

    protected RestClientSupport(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * Выполняет GET-запрос и десериализует ответ в указанный тип.
     *
     * @param url  полный URL запроса
     * @param type класс ожидаемого ответа
     * @param <T>  тип данных в ответе
     * @return десериализованный объект
     * @throws NotFoundException        если ответ 404
     * @throws IllegalArgumentException если другой код ошибки
     */
    protected <T> T getForObject(String url, Class<T> type) {
        try {
            return rest.getForObject(url, type);
        } catch (HttpStatusCodeException e) {
            throw mapException(e, url);
        }
    }

    /**
     * Выполняет HTTP-запрос (обычно GET) и десериализует ответ в обобщённый тип.
     *
     * @param url     полный URL запроса
     * @param method  HTTP-метод
     * @param typeRef тип результата с поддержкой generic-параметров
     * @param <T>     тип данных в ответе
     * @return десериализованный объект
     * @throws NotFoundException        если ответ 404
     * @throws IllegalArgumentException если другой код ошибки
     */
    protected <T> T exchange(String url, HttpMethod method, ParameterizedTypeReference<T> typeRef) {
        try {
            ResponseEntity<T> resp = rest.exchange(url, method, null, typeRef);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw mapException(e, url);
        }
    }

    /**
     * Преобразует HTTP-исключение в доменное исключение.
     */
    private RuntimeException mapException(HttpStatusCodeException e, String url) {
        ApiError api = tryReadApiError(e);
        String msg = api != null ? api.message() : e.getResponseBodyAsString();
        if (e.getStatusCode().value() == 404) {
            return new NotFoundException(msg != null ? msg : ("Resource not found: " + url));
        }
        return new IllegalArgumentException(msg != null ? msg : e.getStatusText());
    }

    /**
     * Пробует десериализовать тело ошибки в {@link ApiError}.
     */
    private ApiError tryReadApiError(HttpStatusCodeException e) {
        try {
            return e.getResponseBodyAs(ApiError.class);
        } catch (Exception ignore) {
            return null;
        }
    }
}
