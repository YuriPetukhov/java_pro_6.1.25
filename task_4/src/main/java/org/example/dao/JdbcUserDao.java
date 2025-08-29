package org.example.dao;

import org.example.domain.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link UserDao}, работающая напрямую с PostgreSQL через JDBC.
 * <p>
 * Внутри содержит небольшие утилиты для выполнения SQL-запросов,
 * чтобы уменьшить дублирование кода при работе с {@link PreparedStatement}.
 */
@Repository
public class JdbcUserDao implements UserDao {
    private final DataSource dataSource;

    /**
     * Конструктор с внедрением источника соединений.
     *
     * @param dataSource пул соединений к БД
     */
    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Универсальный метод для выполнения SELECT-запросов, возвращающих результат.
     *
     * @param sql   текст SQL-запроса
     * @param bind  функция для привязки параметров к {@link PreparedStatement}
     * @param mapper функция для обработки {@link ResultSet}
     * @param <T> тип результата
     * @return результат, полученный от {@code mapper}
     */
    private <T> T queryOne(String sql,
                           SqlConsumer<PreparedStatement> bind,
                           SqlFunction<ResultSet, T> mapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (bind != null) bind.accept(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapper.apply(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выполнении запроса: " + sql, e);
        }
    }

    /**
     * Универсальный метод для выполнения DML-запросов (INSERT/UPDATE/DELETE).
     *
     * @param sql  текст SQL-запроса
     * @param bind функция для привязки параметров к {@link PreparedStatement}
     * @return количество затронутых строк
     */
    private int execUpdate(String sql, SqlConsumer<PreparedStatement> bind) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (bind != null) bind.accept(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выполнении обновления: " + sql, e);
        }
    }

    @FunctionalInterface
    private interface SqlConsumer<T> { void accept(T t) throws SQLException; }

    @FunctionalInterface
    private interface SqlFunction<T, R> { R apply(T t) throws SQLException; }


    /**
     * Создаёт нового пользователя в базе.
     *
     * @param username имя пользователя
     * @return объект {@link User} с присвоенным ID
     */
    @Override
    public User create(String username) {
        String sql = "INSERT INTO app_data.users(username) VALUES (?) RETURNING id";
        Long id = queryOne(sql,
                st -> st.setString(1, username),
                rs -> {
                    if (rs.next()) return rs.getLong(1);
                    throw new SQLException("ID не был возвращён при вставке");
                });
        return new User(id, username);
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор
     * @return {@link Optional} с найденным пользователем или пустой, если не найден
     */
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username FROM app_data.users WHERE id = ?";
        return queryOne(sql,
                st -> st.setLong(1, id),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new User(rs.getLong("id"), rs.getString("username")));
                    }
                    return Optional.empty();
                });
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список {@link User}
     */
    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username FROM app_data.users ORDER BY id";
        return queryOne(sql,
                null,
                rs -> {
                    List<User> users = new ArrayList<>();
                    while (rs.next()) {
                        users.add(new User(rs.getLong("id"), rs.getString("username")));
                    }
                    return users;
                });
    }

    /**
     * Обновляет имя пользователя.
     *
     * @param id идентификатор
     * @param newUsername новое имя
     * @return {@code true}, если обновление прошло успешно
     */
    @Override
    public boolean updateUsername(Long id, String newUsername) {
        String sql = "UPDATE app_data.users SET username = ? WHERE id = ?";
        return execUpdate(sql, st -> {
            st.setString(1, newUsername);
            st.setLong(2, id);
        }) > 0;
    }

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор
     * @return {@code true}, если удаление прошло успешно
     */
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM app_data.users WHERE id = ?";
        return execUpdate(sql, st -> st.setLong(1, id)) > 0;
    }
}
