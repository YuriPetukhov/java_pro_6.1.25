package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PageResponse;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.dto.ProductDto;
import org.example.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST-контроллер платежного сервиса.
 * <p>
 * Предоставляет операции:
 * <ul>
 *     <li>Запрос продуктов пользователя (через интеграцию с product-service)</li>
 *     <li>Исполнение платежа</li>
 * </ul>
 */
@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Методы работы с платежами и интеграцией с продуктами")
public class PaymentController {

    private final PaymentService service;

    /**
     * Запрос продуктов пользователя через платёжный сервис.
     * <p>
     * Контроллер вызывает product-service, получает данные и возвращает клиенту
     * постраничный список {@link ProductDto}.
     *
     * @param userId идентификатор пользователя
     * @param page   номер страницы (опционально)
     * @param size   размер страницы (опционально)
     * @param sort   сортировка в формате "поле,asc|desc" (опционально)
     * @return {@link PageResponse} продуктов пользователя
     */
    @GetMapping("/products")
    @Operation(
            summary = "Запросить продукты пользователя",
            description = "Платёжный сервис проксирует запрос к продукт-сервису и возвращает постраничный список продуктов."
    )
    @ApiResponse(responseCode = "200", description = "Список продуктов успешно получен")
    public PageResponse<ProductDto> getUserProducts(
            @Parameter(description = "Идентификатор пользователя") @RequestParam Long userId,
            @Parameter(description = "Номер страницы (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Размер страницы") @RequestParam(required = false) Integer size,
            @Parameter(description = "Параметры сортировки, напр. balance,desc") @RequestParam(required = false) String sort
    ) {
        return service.fetchUserProducts(userId, page, size, sort);
    }

    /**
     * Исполнение платежа.
     * <p>
     * В процессе:
     * <ul>
     *     <li>Проверяется существование выбранного продукта</li>
     *     <li>Проверяется достаточность средств на балансе</li>
     *     <li>Возвращается результат: успешный платёж или отказ</li>
     * </ul>
     *
     * @param request DTO с параметрами платежа
     * @return результат исполнения платежа
     */
    @PostMapping
    @Operation(
            summary = "Исполнить платёж",
            description = "Выбор продукта, проверка его существования и достаточности средств, исполнение транзакции."
    )
    @ApiResponse(responseCode = "200", description = "Платёж успешно обработан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса или недостаточно средств")
    @ApiResponse(responseCode = "404", description = "Продукт не найден")
    public PaymentResponse makePayment(@Valid @RequestBody PaymentRequest request) {
        return service.execute(request);
    }
}
