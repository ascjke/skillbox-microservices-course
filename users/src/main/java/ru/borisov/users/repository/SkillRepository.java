package ru.borisov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.users.model.Skill;

import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByTitleIgnoreCase(String title);
}
