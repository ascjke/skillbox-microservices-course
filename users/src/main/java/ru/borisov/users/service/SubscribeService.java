package ru.borisov.users.service;

import java.util.UUID;

public interface SubscribeService {
    void requestSubscription(UUID currentUserId, UUID followingUserId);

    void confirmSubscription(UUID userId, UUID followerUserId);

    void unsubscribeUser(UUID userId, UUID followingUserId);
}
