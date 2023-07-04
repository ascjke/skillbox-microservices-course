package ru.borisov.users.service;

import java.util.List;
import java.util.UUID;

public interface FollowService {
    void follow(UUID currentUserId, UUID followingUserId);

    void unfollow(UUID userId, UUID followingUserId);

}
