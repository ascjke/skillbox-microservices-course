package ru.borisov.users.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.Male;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User savedUser;
    private UUID savedUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        savedUserId = UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9813");
        user = User.builder()
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
                .build();

        savedUser = User.builder()
                .id(savedUserId)
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
                .build();
    }

    @Test
    void createUser_shouldreturnUser_whenLoginIsNotBusy() {
        // given
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .username("ascjke")
                .email("ascjke@mail.ru")
                .password("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/8oY4FmU7FkjqaO")
                .build();

        doNothing().when(validationUtils).validateRequest(registerUserRequest);
        when(userRepository.save(user)).thenReturn(savedUser);

        //when
        User result = userService.registerUser(registerUserRequest);

        //then
        Assertions.assertEquals(savedUser, result);
    }

    @Test
    void createUser_shouldThrow_whenLoginIsBusy() {
        // given
        when(userRepository.existsByUsername("ascjke")).thenReturn(true);
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .username("ascjke")
                .email("ascjke@mail.ru")
                .password("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/8oY4FmU7FkjqaO")
                .build();
        doNothing().when(validationUtils).validateRequest(registerUserRequest);


        //then
        assertThrows(CommonException.class, () -> {
            // when
            userService.registerUser(registerUserRequest);
        });
    }

    @Test
    void updateUserInfo_shouldUpdateUserInfo_whenInfoIsDifferent() {
        // given
        UpdateUserInfoRequest updateUserInfoRequest = UpdateUserInfoRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .male(Male.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Москва") // поменялся город
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .build();

        User updatedUser = User.builder()
                .id(savedUserId)
                .username("ascjke")
                .email("ascjke@mail.ru")
                .passwordHash("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/8oY4FmU7FkjqaO")
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .male(Male.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Москва") // поменялся город
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .build();

        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // when
        User result = userService.updateUserInfo(updateUserInfoRequest, savedUserId);

        // then
        verify(userRepository, times(1)).save(updatedUser);
        Assertions.assertEquals(updatedUser, result);
    }

    @Test
    void updateUserInfo_noChanges_shouldReturnSameUser() {
        // given
        UpdateUserInfoRequest updateUserInfoRequest = UpdateUserInfoRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .male(Male.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Якутск") // поменялся город
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .build();


        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        User result = userService.updateUserInfo(updateUserInfoRequest, savedUserId);

        // then
        verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        Assertions.assertEquals(savedUser, result);
    }

    @Test
    void getUserById_existingUser_shouldReturnUser() {
        // given
        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        User result = userService.getUserById(savedUserId);

        // then
        Assertions.assertEquals(savedUser, result);
    }

    @Test
    void getUserById_nonExistingUser_shouldThrowException() {
        // given
        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.empty());

        // when / then
        Assertions.assertThrows(CommonException.class, () -> {
            userService.getUserById(savedUserId);
        });
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        // given
        List<User> users = List.of(savedUser);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> result = userService.getAllUsers();

        // then
        Assertions.assertEquals(users, result);
    }

    @Test
    void removeUserById_existingUser_shouldRemoveUser() {
        // given
        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));
        doNothing().when(userRepository).delete(savedUser);

        // when
        ApiResponse response = userService.removeUserById(savedUserId);

        // then
        Assertions.assertTrue(response.success());
        Assertions.assertEquals("Пользователь " + savedUser.getUsername() + " успешно удален!", response.message());
        Mockito.verify(userRepository, Mockito.times(1)).delete(savedUser);
    }

    @Test
    void getUserFollowers() {
        // given
        savedUser.getFollowers().add(Follower.builder()
                .from(user)
                .to(savedUser)
                .build());

        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        Set<User> result = userService.getUserFollowers(savedUserId);

        // then
        Assertions.assertEquals(Set.of(user), result);
    }

    @Test
    void getUserFollowing() {
        // given
        savedUser.getFollowing().add(Follower.builder()
                .from(savedUser)
                .to(user)
                .build());

        Mockito.when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        Set<User> result = userService.getUserFollowing(savedUserId);

        // then
        Assertions.assertEquals(Set.of(user), result);
    }
}