package ru.borisov.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.Following;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ValidationUtils validationUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(RegisterUserRequest request) {

        log.info("Запрос на создание нового пользователя. Тело запроса: {} ", request::toString);
        validationUtils.validateRequest(request);
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .followers(new HashSet<>())
                .followings(new HashSet<>())
                .build();

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CommonException(Code.LOGIN_BUSY,
                    "Пользователь с ником " + request.getUsername() + " уже существует!",
                    HttpStatus.BAD_REQUEST);
        }

        user = userRepository.save(user);

        log.info("Добавлен новый пользователь: {} ", user::getUsername);
        return user;
    }

    @Override
    @Transactional
    public User updateUserInfo(UpdateUserInfoRequest request, UUID uuid) {

        log.info("Запрос на обновление данных пользователя c id={}. Тело запроса: {} ", uuid::toString, request::toString);

        User user = getUserById(uuid);

        if (!user.isInfoUpdated(request)) {
            log.info("Запрос не обновил данные пользователя");
            return user;
        }

        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setMale(request.getMale());
        user.setBirthDate(request.getBirthDate());
        user.setCity(request.getCity());
        user.setProfileImage(request.getProfileImage());
        user.setBio(request.getBio());
        user.setPhone(request.getPhone());

        user = userRepository.save(user);
        log.info("Данные пользователя {} обновлены", user::getUsername);

        return user;
    }

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
    public User getUserById(UUID uuid) {

        return userRepository.findById(uuid)
                .orElseThrow(() -> new CommonException(Code.USER_NOT_FOUND,
                        "Пользователя с id=" + uuid + " не существует!",
                        HttpStatus.BAD_REQUEST));
    }


    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    @Transactional
    public ApiResponse removeUserById(UUID uuid) {

        User user = getUserById(uuid);
        userRepository.delete(user);
        log.info("Пользователь {} успешно удален!", user::getUsername);
        return new ApiResponse(true, "Пользователь " + user.getUsername() + " успешно удален");
    }

    @Override
    public List<Follower> getUserFollowers(UUID uuid) {
        return getUserById(uuid)
                .getFollowers().stream()
                .filter(follower -> follower.isConfirmed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Following> getUserFollowings(UUID uuid) {
        return getUserById(uuid)
                .getFollowings().stream()
                .filter(following -> following.isConfirmed())
                .collect(Collectors.toList());
    }

}
