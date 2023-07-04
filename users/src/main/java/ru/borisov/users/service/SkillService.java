package ru.borisov.users.service;

import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.model.Skill;

import java.util.UUID;

public interface SkillService {

    Skill addSkillToUser(AddSkillRequest request, UUID id);

    void removeSkillFromUser(UUID userId, UUID skillId);
}
