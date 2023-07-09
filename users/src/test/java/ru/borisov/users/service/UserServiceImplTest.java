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
import ru.borisov.users.model.Subscription;
import ru.borisov.users.model.Gender;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.util.ValidationUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                .gender(Gender.MALE)
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
                .gender(Gender.MALE)
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
        assertEquals(savedUser, result);
    }

    @Test
    void createUser_shouldThrowException_whenLoginIsBusy() {
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
                .gender(Gender.MALE)
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
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Москва") // поменялся город
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .build();

        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // when
        User result = userService.updateUserInfo(updateUserInfoRequest, savedUserId);

        // then
        verify(userRepository, times(1)).save(updatedUser);
        assertEquals(updatedUser, result);
    }

    @Test
    void updateUserInfo_shouldNotCallDatabase_whenInfoIsSame() {
        // given
        UpdateUserInfoRequest updateUserInfoRequest = UpdateUserInfoRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 6, 23))
                .city("Якутск") // поменялся город
                .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                .bio("nothing to say")
                .phone("89141002304567")
                .build();


        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        User result = userService.updateUserInfo(updateUserInfoRequest, savedUserId);

        // then
        verify(userRepository, Mockito.times(0)).save(any(User.class));
        assertEquals(savedUser, result);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // given
        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        User result = userService.getUserById(savedUserId);

        // then
        assertEquals(savedUser, result);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotExist() {
        // given
        when(userRepository.findById(savedUserId)).thenReturn(Optional.empty());

        // then
        assertThrows(CommonException.class, () -> {
            // when
            userService.getUserById(savedUserId);
        });
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        // given
        List<User> users = List.of(savedUser);
        when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> result = userService.getAllUsers();

        // then
        assertEquals(users, result);
    }

    @Test
    void removeUserById_shouldRemoveUser_whenUserExists() {
        // given
        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));
        doNothing().when(userRepository).delete(savedUser);

        // when
        ApiResponse response = userService.removeUserById(savedUserId);

        // then
        Assertions.assertTrue(response.success());
        assertEquals("Пользователь " + savedUser.getUsername() + " успешно удален!", response.message());
        verify(userRepository, Mockito.times(1)).delete(savedUser);
    }

    @Test
    void getUserFollowers_shouldReturnFollowers_whenTheyExist() {
        // given
        savedUser.getFollowers().add(Subscription.builder()
                .from(user)
                .to(savedUser)
                .build());

        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        Set<User> result = userService.getUserFollowers(savedUserId);

        // then
        assertEquals(Set.of(user), result);
    }

    @Test
    void getUserFollowing_shouldReturnFollowing_whenTheyExist() {
        // given
        savedUser.getFollowing().add(Subscription.builder()
                .from(savedUser)
                .to(user)
                .build());

        when(userRepository.findById(savedUserId)).thenReturn(Optional.of(savedUser));

        // when
        Set<User> result = userService.getUserFollowing(savedUserId);

        // then
        assertEquals(Set.of(user), result);
    }
}