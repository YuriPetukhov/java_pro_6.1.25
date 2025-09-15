package org.example.integrations;

import org.example.dto.PageResponse;
import org.example.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Клиент для взаимодействия с сервисом продуктов.
 * <p>
 * Использует {@link RestTemplate} для выполнения HTTP-запросов.
 * Оборачивает вызовы к API product-service и возвращает DTO-модели.
 */
@Component
public class ProductClient extends RestClientSupport {

    private final String baseUrl;

    /**
     * Конструктор клиента продуктов.
     *
     * @param rest    экземпляр {@link RestTemplate}, сконфигурированный в Spring-контексте
     * @param baseUrl базовый URL сервиса продуктов (например, http://localhost:8081/api/v1)
     */
    public ProductClient(RestTemplate rest, @Value("${product.base-url}") String baseUrl) {
        super(rest);
        this.baseUrl = baseUrl;
    }

    /**
     * Получает продукт по его идентификатору.
     *
     * @param id идентификатор продукта
     * @return DTO продукта
     * @throws org.example.exception.NotFoundException если продукт не найден (404)
     * @throws IllegalArgumentException при других ошибках ответа
     */
    public ProductDto getProductById(Long id) {
        String url = baseUrl + "/products/" + id;
        return get(url, ProductDto.class);
    }

    /**
     * Получает постраничный список продуктов пользователя.
     * <p>
     * В запрос можно передать параметры пагинации и сортировки.
     *
     * @param userId идентификатор пользователя
     * @param page   номер страницы (0-based), опционально
     * @param size   размер страницы, опционально
     * @param sort   сортировка в формате "поле,asc|desc", опционально
     * @return {@link PageResponse} с продуктами пользователя
     * @throws IllegalArgumentException при ошибках ответа сервиса продуктов
     */
    public PageResponse<ProductDto> getProductsByUser(Long userId, Integer page, Integer size, String sort) {
        String url = UrlBuilderUtil.buildPagedUrl(baseUrl, "/products", userId, page, size, sort);
        return get(url, new ParameterizedTypeReference<PageResponse<ProductDto>>() {});
    }
}
