package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.User;

import java.util.Optional;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    Optional<Follower> findByUser(User user);
}
