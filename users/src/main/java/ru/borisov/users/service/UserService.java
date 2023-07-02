package ru.borisov.users.service;

import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.Following;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User registerUser(RegisterUserRequest request);

    User updateUserInfo(UpdateUserInfoRequest request, UUID uuid);

    User getUserById(UUID uuid);

    List<User> getAllUsers();

    ApiResponse removeUserById(UUID uuid);

    List<Follower> getUserFollowers(UUID uuid);

    List<Following> getUserFollowings(UUID uuid);

    Skill addSkillToUser(AddSkillRequest request, UUID id);
}
