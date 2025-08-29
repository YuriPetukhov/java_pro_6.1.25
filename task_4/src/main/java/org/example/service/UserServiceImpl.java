package org.example.service;

import jakarta.transaction.Transactional;
import org.example.repository.UserRepository;
import org.example.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 * Реализует бизнес-логику поверх {@link UserRepository}.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    /**
     * Конструктор сервиса.
     *
     * @param repo репозиторий для доступа к данным пользователей
     */
    public UserServiceImpl(UserRepository repo) { this.repo = repo; }

    /**
     * Создать нового пользователя.
     *
     * @param username имя пользователя
     * @return созданный объект {@link User}
     */
    @Override
    public User createUser(String username) {
        return repo.save(new User(username));
    }

    /**
     * Найти пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return {@link Optional} с пользователем или пустое значение, если не найден
     */
    @Override
    public Optional<User> getUser(Long id) { return repo.findById(id); }

    /**
     * Получить список всех пользователей.
     *
     * @return список {@link User}
     */
    @Override
    public List<User> getAll() { return repo.findAll(); }

    /**
     * Переименовать пользователя.
     *
     * @param id      идентификатор пользователя
     * @param newName новое имя пользователя
     */
    @Override
    public void rename(Long id, String newName) {
        repo.updateUsername(id, newName);
    }

    /**
     * Удалить пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) return;
        repo.deleteById(id);
    }
}
