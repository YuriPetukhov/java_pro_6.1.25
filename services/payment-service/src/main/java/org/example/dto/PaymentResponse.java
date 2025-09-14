package org.example.dto;

import org.example.enums.PaymentStatus;

import java.math.BigDecimal;

/**
 * DTO для ответа о результате платежа.
 *
 * @param productId ID продукта, по которому выполнялся платёж
 * @param amount    сумма платежа (null, если отклонён)
 * @param status    статус платежа (APPROVED/DECLINED)
 * @param message   дополнительное описание результата
 */
public record PaymentResponse(
        Long productId,
        BigDecimal amount,
        PaymentStatus status,
        String message
) {
}
