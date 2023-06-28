package ru.borisov.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.controller.request.EditUserRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ValidationUtils validationUtils;

    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {

        log.info("Запрос на создание нового пользователя. Тело запроса: {} ", request::toString);
        validationUtils.validateRequest(request);
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(request.getPasswordHash())
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .male(request.getMale())
                .birthDate(request.getBirthDate())
                .city(request.getCity())
                .profileImage(request.getProfileImage())
                .bio(request.getBio())
                .phone(request.getPhone())
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
    public User editUser(EditUserRequest request, UUID uuid) {

        log.info("Запрос на обновление данных пользователя. Тело запроса: {} ", request::toString);

        User user = getUserById(uuid);
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

        return new ApiResponse(true, "Пользователь " + user.getUsername() + " успешно удален");
    }
}
