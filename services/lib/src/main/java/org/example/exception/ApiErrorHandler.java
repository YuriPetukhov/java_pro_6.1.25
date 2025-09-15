package org.example.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.error.ApiError;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Клиентский обработчик ошибок для {@link org.springframework.web.client.RestTemplate}.
 * <p>
 * Перехватывает ответы с кодами 4xx/5xx и:
 * <ul>
 *   <li>пытается прочитать тело ошибки как {@link ApiError} и взять из него сообщение;</li>
 *   <li>отображает 404 в {@link NotFoundException};</li>
 *   <li>прочие ошибки — в {@link IllegalArgumentException} с сообщением из тела/статуса.</li>
 * </ul>
 * Регистрируется в конфигурации клиента: <pre>
 * restTemplate.setErrorHandler(new ApiErrorHandler(objectMapper));
 * </pre>
 * Потокобезопасен при условии потокобезопасного {@link ObjectMapper}.
 */
public class ApiErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper om;

    /**
     * @param om {@link ObjectMapper}, используемый для десериализации {@link ApiError} из тела ответа
     */
    public ApiErrorHandler(ObjectMapper om) {
        this.om = om;
    }

    /**
     * Сообщает, что любой статус из диапазона ошибок (4xx/5xx) считается ошибкой.
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    /**
     * Преобразует HTTP-ошибку удалённого сервиса в доменные исключения.
     * <p>
     * Предпочитает сообщение из {@link ApiError}, при его отсутствии — статусный текст ответа.
     */
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ApiError body = null;

        // Пробуем прочитать тело как ApiError
        try (InputStream is = response.getBody()) {
            if (is != null) {
                body = om.readValue(is, ApiError.class);
            }
        } catch (Exception ignore) {
            // Если тело не читается — просто используем статусный текст
        }

        String msg = (body != null && body.message() != null && !body.message().isBlank())
                ? body.message()
                : response.getStatusText();

        int code = response.getStatusCode().value();
        if (code == HttpStatus.NOT_FOUND.value()) {
            throw new NotFoundException(msg);
        }

        // При необходимости здесь можно добавить дополнительные маппинги
        throw new IllegalArgumentException(msg);
    }
}
