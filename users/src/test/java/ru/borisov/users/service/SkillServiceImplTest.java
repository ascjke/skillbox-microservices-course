package ru.borisov.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.model.Male;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.SkillType;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkillServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private SkillServiceImpl skillService;

    private User user;
    private UUID userId;
    private UUID skillId;
    private AddSkillRequest addSkillRequest;
    private Skill skill;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9813");
        skillId = UUID.fromString("4d5d6056-980a-45e2-be04-9df962af9213");
        user = User.builder()
                .id(userId)
                .username("ascjke")
                .email("ascjke@mail.ru")
                .passwordHash("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/8oY4FmU7FkjqaO")
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .male(Male.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Якутск")
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .followers(new HashSet<>())
                .following(new HashSet<>())
                .skills(new HashSet<>())
                .build();

        addSkillRequest = AddSkillRequest.builder()
                .title("java17")
                .skillType(SkillType.HARD_SKILL)
                .build();

        skill = Skill.builder()
                .title("java17")
                .skillType(SkillType.HARD_SKILL)
                .build();
    }

    @Test
    void addSkillToUser_shouldNotSaveSkill_whenSkillExists() {

        // given
        doNothing().when(validationUtils).validateRequest(addSkillRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findByTitleIgnoreCase("java17")).thenReturn(Optional.of(skill));

        // when
        Skill result = skillService.addSkillToUser(addSkillRequest, userId);

        // then
        assertTrue(user.getSkills().contains(result));
        verify(userRepository, times(1)).save(user);
        verify(skillRepository, times(0)).save(result);
    }

    @Test
    void addSkillToUser_shouldSaveSkill_whenSkillNotExist() {

        // given
        doNothing().when(validationUtils).validateRequest(addSkillRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findByTitleIgnoreCase("java17")).thenReturn(Optional.empty());
        when(skillRepository.save(skill)).thenReturn(skill);

        // when
        Skill result = skillService.addSkillToUser(addSkillRequest, userId);

        // then
        assertTrue(user.getSkills().contains(result));
        verify(userRepository, times(1)).save(user);
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void removeSkillFromUser_shouldRemoveSkill_whenSkillExists() {

        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        user.getSkills().add(skill);

        // when
        skillService.removeSkillFromUser(userId, skillId);

        // then
        assertFalse(user.getSkills().contains(skill));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void removeSkillFromUser_shouldThrowException_whenSkillNotExist() {

        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        // then
        assertThrows(CommonException.class, () -> {
            // when
            skillService.removeSkillFromUser(userId, skillId);
        });
    }
}