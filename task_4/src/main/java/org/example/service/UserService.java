package org.example.service;

import org.example.dao.UserDao;
import org.example.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с пользователями.
 * <p>
 * Инкапсулирует бизнес-логику и обращается к {@link UserDao}
 * для выполнения CRUD-операций с базой данных.
 */
@Service
public class UserService {
    private final UserDao dao;

    /**
     * Создаёт сервис на основе переданной реализации {@link UserDao}.
     *
     * @param dao DAO для доступа к данным о пользователях
     */
    public UserService(UserDao dao) { this.dao = dao; }

    /**
     * Создаёт нового пользователя.
     *
     * @param username имя пользователя
     * @return созданный объект {@link User} с присвоенным ID
     */
    public User createUser(String username) { return dao.create(username); }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return {@link Optional}, содержащий пользователя при успешном поиске,
     * или пустой, если пользователь не найден
     */
    public Optional<User> getUser(Long id) { return dao.findById(id); }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список объектов {@link User}
     */
    public List<User> getAll() { return dao.findAll(); }

    /**
     * Изменяет имя пользователя.
     *
     * @param id      идентификатор пользователя
     * @param newName новое имя
     * @return {@code true}, если обновление прошло успешно
     */
    public boolean rename(Long id, String newName) { return dao.updateUsername(id, newName); }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return {@code true}, если удаление прошло успешно
     */
    public boolean delete(Long id) { return dao.delete(id); }
}
