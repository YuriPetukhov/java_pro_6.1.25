package org.example.dao;

import org.example.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(String username);
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean updateUsername(Long id, String newUsername);
    boolean delete(Long id);
}
