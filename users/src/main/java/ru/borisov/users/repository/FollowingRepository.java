package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Following;
import ru.borisov.users.model.User;

import java.util.Optional;
import java.util.UUID;

public interface FollowingRepository extends JpaRepository<Following, UUID> {

}
