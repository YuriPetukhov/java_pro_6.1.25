package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.enums.ProductType;

import java.math.BigDecimal;

/**
 * Сущность продукта.
 * Представляет банковский или финансовый продукт, принадлежащий пользователю.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_products_account",
                columnNames = "account_number")
)
public class Product {

    /**
     * Уникальный идентификатор продукта (генерируется автоматически).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер счёта, связанный с продуктом.
     * Уникален в пределах таблицы.
     */
    @Column(name = "account_number", nullable = false, length = 32)
    private String accountNumber;

    /**
     * Баланс продукта.
     * Хранится с точностью до 2 знаков после запятой.
     * По умолчанию равен 0.00.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Тип продукта (например, депозит, кредит).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductType type;

    /**
     * Пользователь, которому принадлежит данный продукт.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_user"))
    private User user;

    /**
     * Конструктор для инициализации нового продукта.
     *
     * @param accountNumber номер счёта
     * @param balance       баланс
     * @param type          тип продукта
     * @param user          владелец продукта
     */
    public Product(String accountNumber, BigDecimal balance, ProductType type, User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.type = type;
        this.user = user;
    }

    /**
     * Возвращает строковое представление продукта.
     * <p>
     * Включает основные поля: id, accountNumber, balance, type и userId.
     * Полный объект {@link User} не выводится, чтобы избежать рекурсивных вызовов.
     *
     * @return строка вида:
     *         Product[id=..., accountNumber=..., balance=..., type=..., userId=...]
     */
    @Override
    public String toString() {
        return "Product[id=%s, accountNumber=%s, balance=%s, type=%s, userId=%s]"
                .formatted(id, accountNumber, balance, type,
                        user != null ? user.getId() : null);
    }
}
