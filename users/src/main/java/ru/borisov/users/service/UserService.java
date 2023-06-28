package ru.borisov.users.service;

import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.controller.request.EditUserRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(CreateUserRequest request);

    User editUser(EditUserRequest request, UUID uuid);

    User getUserById(UUID uuid);

    List<User> getAllUsers();

    ApiResponse removeUserById(UUID uuid);
}
