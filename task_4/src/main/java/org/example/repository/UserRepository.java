package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Query("update User u set u.username = :username where u.id = :id")
    void updateUsername(@Param("id") Long id, @Param("username") String username);
}
