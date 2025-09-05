package org.example.service.impl;

import org.example.repository.UserRepository;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- спринговый

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 * Реализует бизнес-логику поверх {@link UserRepository}.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Конструктор сервиса.
     *
     * @param userRepository репозиторий для доступа к данным пользователей
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создать нового пользователя.
     *
     * @param username имя пользователя
     * @return созданный объект {@link User}
     */
    @Override
    @Transactional
    public User createUser(String username) {
        return userRepository.save(new User(username));
    }

    /**
     * Найти пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return {@link Optional} с пользователем или пустое значение, если не найден
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список {@link User}
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Переименовать пользователя.
     *
     * @param id      идентификатор пользователя
     * @param newName новое имя пользователя
     */
    @Override
    @Transactional
    public void rename(Long id, String newName) {
        userRepository.updateUsername(id, newName);
    }

    /**
     * Удалить пользователя по идентификатору.
     * <p>
     * Операция идемпотентна: если пользователя с указанным идентификатором нет,
     * метод не выбрасывает исключение и завершает выполнение без ошибок.
     *
     * @param id идентификатор пользователя
     */
    @Override
    @Transactional
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (org.springframework.dao.EmptyResultDataAccessException ignored) {
        }
    }

    /**
     * Проверить наличие пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь существует, иначе false
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
