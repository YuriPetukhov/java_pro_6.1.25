package org.example.dto;

import jakarta.validation.constraints.*;
import org.example.enums.ProductType;

import java.math.BigDecimal;

/**
 * DTO для создания нового продукта.
 * Используется в запросах REST API при добавлении продукта.
 *
 * @param userId        идентификатор пользователя, которому принадлежит продукт
 * @param accountNumber номер счёта (максимум 32 символа, не может быть пустым)
 * @param balance       текущий баланс (не может быть меньше 0.00)
 * @param type          тип продукта (например, депозит, кредит и т.д.)
 */
public record CreateProductDTO(
        @NotNull Long userId,
        @NotBlank @Size(max = 32) String accountNumber,
        @NotNull @DecimalMin(value = "0.00") BigDecimal balance,
        @NotNull ProductType type
) {}
