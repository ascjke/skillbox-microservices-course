package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.borisov.users.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users_scheme._user WHERE username = :username)", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);

    Optional<User> findByUsername(String username);
}
