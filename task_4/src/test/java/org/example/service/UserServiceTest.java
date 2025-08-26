package org.example.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.dao.JdbcUserDao;
import org.example.dao.UserDao;
import org.example.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserServiceTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("app_user")
                    .withPassword("app_password");

    static DataSource ds;
    static UserService service;

    @BeforeAll
    static void setUp() {
        var cfg = new HikariConfig();
        cfg.setJdbcUrl(POSTGRES.getJdbcUrl());
        cfg.setUsername(POSTGRES.getUsername());
        cfg.setPassword(POSTGRES.getPassword());
        cfg.setMaximumPoolSize(4);
        ds = new HikariDataSource(cfg);

        var jdbc = new JdbcTemplate(ds);
        jdbc.execute("CREATE SCHEMA IF NOT EXISTS app_data AUTHORIZATION CURRENT_USER");
        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS app_data.users(
          id BIGSERIAL PRIMARY KEY,
          username VARCHAR(255) UNIQUE NOT NULL
        )
        """);

        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(ds);
        UserDao dao = new JdbcUserDao(named);
        service = new UserService(dao);
    }

    @BeforeEach
    void cleanDb() {
        new org.springframework.jdbc.core.JdbcTemplate(ds)
                .execute("TRUNCATE TABLE app_data.users RESTART IDENTITY");
    }

    @AfterAll
    static void tearDown() {
        if (ds instanceof HikariDataSource h) h.close();
    }

    @Test
    @DisplayName("Полный CRUD: create → get → list → update → delete")
    void create_get_list_update_delete() {
        User a = service.createUser("alice");
        User b = service.createUser("bob");

        assertNotNull(a.id());
        assertNotNull(b.id());

        assertTrue(service.getUser(a.id()).isPresent());
        List<User> all = service.getAll();
        assertEquals(2, all.size());

        assertTrue(service.rename(b.id(), "bob2"));
        assertEquals("bob2", service.getUser(b.id()).orElseThrow().username());

        assertTrue(service.delete(a.id()));
        assertEquals(1, service.getAll().size());
    }

    @Test
    @DisplayName("Нельзя создать двух пользователей с одинаковым username")
    void unique_username_violation() {
        service.createUser("dup");
        assertThrows(Exception.class, () -> service.createUser("dup"));
    }

    @Test
    @DisplayName("findAll() — пусто, когда в таблице нет записей")
    void find_all_empty_when_no_users() {
        assertTrue(service.getAll().isEmpty());
    }

    @Test
    @DisplayName("getUser(id) — Optional.empty(), когда пользователя нет")
    void get_user_nonexistent_returns_empty() {
        assertTrue(service.getUser(9999L).isEmpty());
    }

    @Test
    @DisplayName("delete(id) — false, когда пользователя нет")
    void delete_nonexistent_returns_false() {
        assertFalse(service.delete(12345L));
    }

    @Test
    @DisplayName("rename(id, name) — false, когда пользователя нет")
    void rename_nonexistent_returns_false() {
        assertFalse(service.rename(777L, "ghost"));
    }

    @Test
    @DisplayName("ID автоинкрементируется: второй > первого")
    void ids_are_incremental() {
        User a = service.createUser("u1");
        User b = service.createUser("u2");
        assertNotNull(a.id());
        assertNotNull(b.id());
        assertTrue(b.id() > a.id());
    }

    @Test
    @DisplayName("Нельзя переименовать в уже существующий username (уникальный индекс)")
    void rename_to_existing_username_violates_unique() {
        User a = service.createUser("alice");
        User b = service.createUser("bob");
        assertThrows(Exception.class, () -> service.rename(b.id(), "alice"));
        assertEquals("bob", service.getUser(b.id()).orElseThrow().username());
    }

}
