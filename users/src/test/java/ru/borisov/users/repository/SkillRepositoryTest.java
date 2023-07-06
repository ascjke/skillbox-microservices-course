package ru.borisov.users.repository;

import org.junit.jupiter.api.Test;
import ru.borisov.users.DatabaseTestContainer;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.SkillType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SkillRepositoryTest extends DatabaseTestContainer {

    @Test
    void findByTitleIgnoreCase_shouldReturnSkill_whenSkillExist() {

        // given
        Skill skill = Skill.builder()
                .title("java8")
                .skillType(SkillType.HARD_SKILL)
                .build();
        skillRepository.save(skill);

        // when
        Optional<Skill> skillOptional = skillRepository.findByTitleIgnoreCase("JaVa8");

        // Then
        assertThat(skillOptional).isPresent();
    }

    @Test
    void findByTitleIgnoreCase_shouldReturnEmptySkill_whenSkillNotExist() {

        // when
        Optional<Skill> skillNotExist = skillRepository.findByTitleIgnoreCase("docker");

        // Then
        assertThat(skillNotExist).isEmpty();
    }
}