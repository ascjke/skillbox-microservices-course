package ru.borisov.users.service;

import ru.borisov.users.model.Follower;

import java.util.List;
import java.util.UUID;

public interface SubscribeService {
    void requestSubscription(UUID currentUserId, UUID followingUserId);

    void confirmSubscription(UUID userId, UUID followerUserId);

    void unsubscribeUser(UUID userId, UUID followingUserId);

    List<Follower> getSubscriptionRequests(UUID id);
}
