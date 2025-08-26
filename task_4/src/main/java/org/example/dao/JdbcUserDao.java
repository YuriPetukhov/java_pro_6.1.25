package org.example.dao;

import org.example.domain.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link UserDao}, использующая JDBC-шаблон Spring.
 * <p>
 * SQL-запросы загружаются из файлов ресурсов, расположенных в {@code src/main/resources/sql/users}.
 */
public class JdbcUserDao implements UserDao {
    private final NamedParameterJdbcTemplate jdbc;

    /**
     * Конструктор, принимающий {@link NamedParameterJdbcTemplate}.
     *
     * @param jdbc JDBC-шаблон с поддержкой именованных параметров
     */
    public JdbcUserDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Загружает SQL-запрос из файла ресурсов по имени.
     * Файл ищется в папке {@code sql/users/}.
     *
     * @param name имя SQL-файла без расширения
     * @return содержимое SQL-запроса в виде строки
     * @throws RuntimeException если файл не найден или не может быть прочитан
     */
    private String sql(String name) {
        try (var in = new ClassPathResource("sql/users/" + name + ".sql").getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("SQL not found: " + name, e);
        }
    }

    /**
     * Создаёт нового пользователя в базе данных.
     *
     * @param username имя пользователя
     * @return объект {@link User} с заполненным ID
     */
    @Override
    public User create(String username) {
        var p = new MapSqlParameterSource().addValue("username", username);
        Long id = jdbc.queryForObject(sql("insert"), p, Long.class);
        return new User(id, username);
    }

    /**
     * Ищет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return {@link Optional} с пользователем, если найден
     */
    @Override
    public Optional<User> findById(Long id) {
        var p = new MapSqlParameterSource().addValue("id", id);
        var list = jdbc.query(sql("select_by_id"), p,
                (rs, rn) -> new User(rs.getLong("id"), rs.getString("username")));
        return list.stream().findFirst();
    }

    /**
     * Возвращает список всех пользователей из таблицы.
     *
     * @return список пользователей
     */
    @Override
    public List<User> findAll() {
        return jdbc.query(sql("select_all"), new MapSqlParameterSource(),
                (rs, rn) -> new User(rs.getLong("id"), rs.getString("username")));
    }

    /**
     * Обновляет имя пользователя по его ID.
     *
     * @param id         идентификатор пользователя
     * @param newUsername новое имя
     * @return {@code true}, если обновление прошло успешно
     */
    @Override
    public boolean updateUsername(Long id, String newUsername) {
        var p = new MapSqlParameterSource().addValue("id", id).addValue("username", newUsername);
        return jdbc.update(sql("update_username"), p) > 0;
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return {@code true}, если удаление прошло успешно
     */
    @Override
    public boolean delete(Long id) {
        var p = new MapSqlParameterSource().addValue("id", id);
        return jdbc.update(sql("delete_by_id"), p) > 0;
    }
}
