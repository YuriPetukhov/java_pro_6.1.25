package org.example.domain;

/**
 * Доменная модель пользователя.
 * <p>
 * Используется в сервисах и DAO для передачи данных
 * о пользователях приложения.
 *
 * @param id       уникальный идентификатор пользователя (PRIMARY KEY в БД)
 * @param username имя пользователя (уникальное, не может быть {@code null})
 */
public record User(Long id, String username) { }
