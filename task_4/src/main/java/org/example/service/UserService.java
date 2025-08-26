package org.example.service;

import org.example.dao.UserDao;
import org.example.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с пользователями.
 * <p>
 * Инкапсулирует бизнес-логику и обращается к {@link UserDao}
 * для выполнения операций с базой данных.
 */
public class UserService {
    private final UserDao dao;

    /**
     * Создаёт сервис на основе DAO.
     *
     * @param dao реализация {@link UserDao} для доступа к данным
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
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return {@link Optional} с пользователем, если найден
     */
    public Optional<User> getUser(Long id) { return dao.findById(id); }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список объектов {@link User}
     */
    public List<User> getAll() { return dao.findAll(); }

    /**
     * Переименовывает пользователя.
     *
     * @param id      идентификатор пользователя
     * @param newName новое имя
     * @return {@code true}, если обновление прошло успешно
     */
    public boolean rename(Long id, String newName) { return dao.updateUsername(id, newName); }

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор пользователя
     * @return {@code true}, если удаление прошло успешно
     */
    public boolean delete(Long id) { return dao.delete(id); }
}
