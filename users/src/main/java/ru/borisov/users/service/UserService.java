package ru.borisov.users.service;

import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.controller.request.EditUserRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    @Transactional
    User createUser(CreateUserRequest request);

    User editUser(EditUserRequest request, UUID uuid);

    User getUserById(UUID uuid);

    List<User> getAllUsers();

    @Transactional
    ApiResponse removeUser(UUID uuid);
}
