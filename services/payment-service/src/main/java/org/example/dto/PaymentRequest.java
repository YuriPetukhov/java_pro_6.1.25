package org.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO-запрос на исполнение платежа.
 * <p>
 * Используется в {@code PaymentController} при приёме POST-запроса от клиента.
 *
 * @param userId    идентификатор пользователя, от имени которого выполняется платёж
 * @param productId идентификатор продукта, с которого списываются средства
 * @param amount    сумма платежа; должна быть больше нуля (валидация через {@link DecimalMin})
 */
public record PaymentRequest(
        @NotNull Long userId,
        @NotNull Long productId,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
