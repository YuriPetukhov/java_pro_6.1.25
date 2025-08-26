package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.dao.JdbcUserDao;
import org.example.dao.UserDao;
import org.example.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Центральный класс конфигурации Spring для приложения.
 * <p>
 * Определяет инфраструктурные бины: {@link DataSource},
 * {@link NamedParameterJdbcTemplate}, {@link UserDao}, {@link UserService}.
 * Загружает параметры подключения к БД из файла {@code application.properties}.
 */
@Configuration
public class AppConfig {

    /**
     * Загружает настройки приложения из файла {@code application.properties},
     * расположенного в classpath.
     *
     * @return объект {@link Properties} с параметрами
     * @throws RuntimeException если файл не удалось прочитать
     */
    private Properties props() {
        try (var in = new ClassPathResource("application.properties").getInputStream()) {
            var p = new Properties();
            p.load(in);
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создаёт и настраивает пул подключений {@link DataSource} на основе HikariCP.
     * <p>
     * Использует параметры из {@code application.properties}:
     * <ul>
     *     <li>{@code jdbc.url}</li>
     *     <li>{@code jdbc.user}</li>
     *     <li>{@code jdbc.pass}</li>
     * </ul>
     *
     * @return сконфигурированный {@link HikariDataSource}
     */
    @Bean
    public DataSource dataSource() {
        var p = props();
        var cfg = new HikariConfig();
        cfg.setJdbcUrl(p.getProperty("jdbc.url"));
        cfg.setUsername(p.getProperty("jdbc.user"));
        cfg.setPassword(p.getProperty("jdbc.pass"));
        cfg.setMaximumPoolSize(10);
        return new HikariDataSource(cfg);
    }

    /**
     * Определяет бин {@link NamedParameterJdbcTemplate} для удобного выполнения SQL-запросов
     * с использованием именованных параметров.
     *
     * @param ds источник данных
     * @return экземпляр {@link NamedParameterJdbcTemplate}
     */
    @Bean
    public NamedParameterJdbcTemplate namedJdbc(DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }

    /**
     * Определяет бин DAO для работы с пользователями.
     * Используется реализация {@link JdbcUserDao}, которая загружает SQL из ресурсов.
     *
     * @param jdbc JDBC-шаблон с поддержкой именованных параметров
     * @return экземпляр {@link UserDao}
     */
    @Bean
    public UserDao userDao(NamedParameterJdbcTemplate jdbc) {
        return new JdbcUserDao(jdbc);
    }

    /**
     * Определяет сервисный слой для работы с пользователями.
     *
     * @param dao DAO для операций с пользователями
     * @return экземпляр {@link UserService}
     */
    @Bean
    public UserService userService(UserDao dao) {
        return new UserService(dao);
    }
}
