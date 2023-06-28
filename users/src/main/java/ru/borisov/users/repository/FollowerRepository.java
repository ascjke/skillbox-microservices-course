package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Follower;

import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {

}
