package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.PageResponse;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.dto.ProductDto;
import org.example.enums.PaymentStatus;
import org.example.integrations.ProductClient;
import org.example.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Реализация сервиса платежей.
 * <p>
 * Отвечает за бизнес-логику:
 * <ul>
 *     <li>Получение продуктов пользователя (через интеграцию с product-service)</li>
 *     <li>Исполнение платежа:
 *         <ul>
 *             <li>Проверка существования продукта</li>
 *             <li>Проверка принадлежности продукта пользователю</li>
 *             <li>Проверка достаточности средств</li>
 *             <li>Возврат результата (APPROVED/DECLINED)</li>
 *         </ul>
 *     </li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ProductClient productClient;

    /**
     * Запрашивает у product-service список продуктов пользователя.
     *
     * @param userId идентификатор пользователя
     * @param page   номер страницы (0-based), опционально
     * @param size   размер страницы, опционально
     * @param sort   сортировка в формате "поле,asc|desc", опционально
     * @return постраничный список продуктов
     */
    @Override
    public PageResponse<ProductDto> fetchUserProducts(Long userId, Integer page, Integer size, String sort) {
        return productClient.getProductsByUser(userId, page, size, sort);
    }

    /**
     * Исполняет платёж: проверяет продукт и баланс.
     * <p>
     * Алгоритм:
     * <ol>
     *     <li>Получить продукт по ID из product-service</li>
     *     <li>Убедиться, что продукт принадлежит указанному пользователю</li>
     *     <li>Проверить, что средств на продукте достаточно</li>
     *     <li>Вернуть результат (APPROVED либо DECLINED)</li>
     * </ol>
     * <p>
     * На данном этапе списание средств в product-service не выполняется —
     * это можно реализовать в будущем через отдельный эндпоинт.
     *
     * @param req запрос с параметрами платежа
     * @return ответ с результатом исполнения платежа
     */
    @Override
    public PaymentResponse execute(PaymentRequest req) {
        ProductDto product = productClient.getProductById(req.productId());

        // Проверка принадлежности продукта пользователю
        if (!Objects.equals(product.userId(), req.userId())) {
            return new PaymentResponse(req.productId(), null,
                    PaymentStatus.DECLINED, "Product doesn't belong to the user");
        }

        // Проверка достаточности средств
        if (product.balance().compareTo(req.amount()) < 0) {
            return new PaymentResponse(req.productId(), null,
                    PaymentStatus.DECLINED, "Insufficient funds");
        }

        // TODO: здесь в будущем должен быть вызов списания средств в product-service
        // (сейчас просто одобряем платёж без фактической транзакции)
        return new PaymentResponse(req.productId(), req.amount(),
                PaymentStatus.APPROVED, "Payment executed");
    }
}
