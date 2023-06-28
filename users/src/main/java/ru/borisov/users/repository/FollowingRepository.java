package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Following;

import java.util.UUID;

public interface FollowingRepository extends JpaRepository<Following, UUID> {

}
