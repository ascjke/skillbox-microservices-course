package ru.borisov.users.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.model.Male;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;

class UserServiceImplTest {


    @Test
    void createUser() {
        // given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ValidationUtils validationUtils = Mockito.mock(ValidationUtils.class);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
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
                .hardSkills(List.of("java", "docker"))
                .phone("89141002304567")
                .build();

        User user = User.builder()
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
                .hardSkills(List.of("java", "docker"))
                .phone("89141002304567")
                .build();

        User savedUser =
                User.builder()
                        .id(UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9813"))
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
                        .hardSkills(List.of("java", "docker"))
                        .phone("89141002304567")
                        .build();

        doNothing().when(validationUtils).validateRequest(createUserRequest);
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        UserService userService = new UserServiceImpl(userRepository, validationUtils);

        //when
        User result = userService.createUser(createUserRequest);

        //then
        Assertions.assertEquals(savedUser, result);
    }

    @Test
    void editUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void removeUser() {
    }
}