package ru.borisov.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
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
                .following(new HashSet<>())
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
    public User getUserById(UUID uuid) {

        return userRepository.findById(uuid)
                .orElseThrow(() -> new CommonException(Code.USER_NOT_FOUND,
                        "Пользователя с id=" + uuid + " не существует!",
                        HttpStatus.NOT_FOUND));
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
        return new ApiResponse(true, "Пользователь " + user.getUsername() + " успешно удален!");
    }

    @Override
    public Set<User> getUserFollowers(UUID uuid) {
        return getUserById(uuid).getFollowers().stream()
                .map(Follower::getFrom)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getUserFollowing(UUID uuid) {
        return getUserById(uuid).getFollowing().stream()
                .map(Follower::getTo)
                .collect(Collectors.toSet());
    }


}
