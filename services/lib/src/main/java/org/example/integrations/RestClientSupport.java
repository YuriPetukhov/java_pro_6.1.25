package org.example.integrations;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Базовый вспомогательный класс для REST-клиентов.
 * <p>
 * Содержит минимальные обёртки над {@link RestTemplate} для выполнения GET-запросов.
 * Обработка ошибок централизована в {@code ApiErrorHandler}, который должен быть
 * установлен через {@code RestTemplate.setErrorHandler(...)} в конфигурации.
 */
public abstract class RestClientSupport {

    /** Клиент HTTP-запросов, настраивается извне (таймауты, error handler и т.д.). */
    protected final RestTemplate rest;

    /**
     * @param rest настроенный {@link RestTemplate}
     */
    protected RestClientSupport(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * Выполняет GET-запрос и десериализует ответ в указанный простой тип.
     *
     * @param url  полный URL запроса
     * @param type класс результата
     * @param <T>  тип данных в ответе
     * @return десериализованное тело ответа (может быть {@code null}, если сервис так ответил)
     */
    protected <T> T get(String url, Class<T> type) {
        ResponseEntity<T> resp = rest.exchange(url, HttpMethod.GET, null, type);
        return resp.getBody();
    }

    /**
     * Выполняет GET-запрос и десериализует ответ в обобщённый тип (List/Map и т.п.).
     *
     * @param url     полный URL запроса
     * @param typeRef ссылка на обобщённый тип результата
     * @param <T>     тип данных в ответе
     * @return десериализованное тело ответа (может быть {@code null}, если сервис так ответил)
     */
    protected <T> T get(String url, ParameterizedTypeReference<T> typeRef) {
        ResponseEntity<T> resp = rest.exchange(url, HttpMethod.GET, null, typeRef);
        return resp.getBody();
    }
}
