package org.example.factory;

import org.example.dto.CreateProductDTO;
import org.example.entity.Product;
import org.example.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Фабрика для создания сущностей {@link Product} из DTO.
 */
@Component
public class ProductFactory {

    /**
     * Создаёт новый продукт на основе данных из {@link CreateProductDTO}.
     * <p>
     * Дополнительно выполняется:
     * <ul>
     *   <li>обрезка пробелов в номере счёта</li>
     *   <li>гарантия, что баланс не будет отрицательным (минимум 0.00)</li>
     * </ul>
     *
     * @param dto  DTO с данными для создания продукта
     * @param user пользователь, которому принадлежит продукт
     * @return новая сущность {@link Product}
     */
    public Product from(CreateProductDTO dto, User user) {
        return new Product(
                dto.accountNumber().trim(),
                dto.balance().max(BigDecimal.ZERO),
                dto.type(),
                user
        );
    }
}
