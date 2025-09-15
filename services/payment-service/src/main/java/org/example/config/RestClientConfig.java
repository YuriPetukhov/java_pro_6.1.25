package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.ApiErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурация клиента для исходящих HTTP-запросов.
 * <p>
 * Создаёт:
 * <ul>
 *   <li>{@link ApiErrorHandler} — клиентский обработчик ошибок, читающий {@code ApiError} из тела ответа;</li>
 *   <li>{@link RestTemplate} — настроенный HTTP-клиент с таймаутами и подключённым обработчиком ошибок.</li>
 * </ul>
 */
@Configuration
public class RestClientConfig {

    /**
     * Бин обработчика ошибок для {@link RestTemplate}.
     *
     * @param objectMapper общий {@link ObjectMapper}, сконфигурированный Spring Boot
     * @return экземпляр {@link ApiErrorHandler}
     */
    @Bean
    public ApiErrorHandler apiErrorHandler(ObjectMapper objectMapper) {
        return new ApiErrorHandler(objectMapper);
    }

    /**
     * Бин {@link RestTemplate} с базовыми таймаутами и подключённым {@link ApiErrorHandler}.
     * Используется лёгкая фабрика {@link SimpleClientHttpRequestFactory} (без пула соединений).
     * При необходимости connection pooling можно перейти на Apache HttpClient5.
     *
     * @param handler обработчик ошибок для преобразования 4xx/5xx в доменные исключения
     * @return настроенный {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate(ApiErrorHandler handler) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000); // мс до установления соединения
        factory.setReadTimeout(5_000);    // мс ожидания ответа

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(handler);
        return restTemplate;
    }
}
