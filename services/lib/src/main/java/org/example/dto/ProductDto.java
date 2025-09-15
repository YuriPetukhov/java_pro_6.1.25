package org.example.dto;

import org.example.enums.ProductType;

import java.math.BigDecimal;

/**
 * DTO для передачи информации о продукте.
 * Используется в ответах REST API.
 *
 * @param id            уникальный идентификатор продукта
 * @param accountNumber номер счёта, связанный с продуктом
 * @param balance       текущий баланс продукта
 * @param type          тип продукта (в данном случае банковский счёт или карта)
 * @param userId        идентификатор пользователя, владельца продукта
 */
public record ProductDto(
        Long id,
        String accountNumber,
        BigDecimal balance,
        ProductType type,
        long userId
) {
}
