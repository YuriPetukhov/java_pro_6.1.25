package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность пользователя.
 * Представляет владельца продуктов в системе.
 */
@Getter
@Setter
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_username",
                columnNames = "username"
        )
)
public class User {

    /**
     * Уникальный идентификатор пользователя (генерируется автоматически).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column(nullable = false, length = 255)
    private String username;

    /**
     * Защищённый конструктор без аргументов (требуется JPA).
     */
    protected User() {}

    /**
     * Конструктор для создания пользователя с именем.
     *
     * @param username имя пользователя
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Возвращает строковое представление пользователя.
     *
     * @return строка вида "User[id=..., username=...]"
     */
    @Override
    public String toString() {
        return "User[id=%s, username=%s]".formatted(id, username);
    }
}
