package ru.borisov.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class SkillServiceImpl implements SkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;


    @Override
    @Transactional
    public Skill addSkillToUser(AddSkillRequest request, UUID id) {

        log.info("Запрос на добавление навыка от пользователя c id={}. Тело запроса: {} ", id::toString, request::toString);
        User user = getUserById(id);
        Skill skill;
        Optional<Skill> skillOptional = skillRepository.findByTitleIgnoreCase(request.getTitle());
        if (skillOptional.isPresent()) {
            skill = skillOptional.get();
            user.getSkills().add(skill);
            userRepository.save(user);

            log.info("Пользователь {} добавил навык {}", user::getUsername, skill::getTitle);
            return skill;
        }

        skill = Skill.builder()
                .title(request.getTitle().toLowerCase()) // сохраняем все в нижнем регистре
                .skillType(request.getSkillType())
                .build();
        skill = skillRepository.save(skill);
        user.getSkills().add(skill);
        userRepository.save(user);
        log.info("Пользователь {} добавил навык {}", user::getUsername, skill::getTitle);

        return skill;
    }

    @Override
    @Transactional
    public void removeSkillFromUser(UUID userId, UUID skillId) {
        log.info("Запрос на удаление навыка от пользователя c id={}. id накыва {} ", userId::toString, skillId::toString);
        User user = getUserById(userId);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new CommonException(Code.SKILL_NOT_FOUND,
                        "Навыка с id=" + skillId + " не существует!",
                        HttpStatus.BAD_REQUEST));

        user.getSkills().remove(skill);
        userRepository.save(user);

        log.info("Пользователь {} удалил у себя навык {}", user::getUsername, skill::getTitle);
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CommonException(Code.USER_NOT_FOUND,
                        "Пользователя с id=" + id + " не существует!",
                        HttpStatus.BAD_REQUEST));
    }
}
