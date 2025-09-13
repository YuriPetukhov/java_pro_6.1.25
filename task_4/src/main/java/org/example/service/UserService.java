package org.example.service;

import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String username);

    List<User> getAll();

    void rename(Long id, String newName);

    void delete(Long id);

    Optional<User> findById(Long userId);

    boolean existsById(Long userId);
}
