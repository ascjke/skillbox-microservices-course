package ru.borisov.users.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.borisov.users.DatabaseTestContainer;
import ru.borisov.users.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends DatabaseTestContainer {

    @Autowired
    private UserRepository userRepository;


    @Test
    void existsByUsername_shouldReturnTrue_wenUserExists() {

        // given
        String username = "user";
        User user = User.builder()
                .email("test@mail.ru")
                .username(username)
                .passwordHash("password")
                .build();
        userRepository.save(user);

        // when
        boolean userExists = userRepository.existsByUsername(username);

        // Then
        assertTrue(userExists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserNotExist() {
        // given
        String username = "user_not_existing";

        // When
        boolean userExists = userRepository.existsByUsername(username);

        // then
        assertFalse(userExists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_afterSoftDelete() {
        // given
        String username = "user";
        User user = User.builder()
                .email("test@mail.ru")
                .username(username)
                .passwordHash("password")
                .build();
        userRepository.save(user);

        // When
        userRepository.delete(user);
        boolean userExists = userRepository.existsByUsername(username);

        // then
        assertFalse(userExists);
    }
}