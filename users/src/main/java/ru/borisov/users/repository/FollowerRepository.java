package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Subscription;
import ru.borisov.users.model.User;

import java.util.Optional;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByFromAndTo(User from, User to);
}
