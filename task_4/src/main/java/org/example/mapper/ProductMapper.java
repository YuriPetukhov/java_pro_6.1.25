package org.example.mapper;

import org.example.dto.ProductDto;
import org.example.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования сущностей {@link Product} в DTO {@link ProductDto}.
 */
@Component
public class ProductMapper {

    /**
     * Преобразует сущность продукта в DTO.
     * <p>
     * Перед маппингом проверяется, что у продукта есть владелец
     * и у {@link org.example.entity.User} задан идентификатор.
     * В противном случае выбрасывается {@link IllegalStateException}.
     *
     * @param product сущность продукта
     * @return DTO с данными продукта
     * @throws IllegalStateException если у продукта отсутствует пользователь или его ID
     */
    public ProductDto toDto(Product product) {
        if (product.getUser() == null || product.getUser().getId() == null) {
            throw new IllegalStateException("Product must have a non-null User with an ID");
        }
        return new ProductDto(
                product.getId(),
                product.getAccountNumber(),
                product.getBalance(),
                product.getType(),
                product.getUser().getId()
        );
    }
}
