package ru.borisov.users.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.borisov.users.DatabaseTestContainer;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.model.Male;
import ru.borisov.users.model.User;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class UserControllerTest extends DatabaseTestContainer {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private User user;
    private static final String USERS_URL = "/api/users";
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user =
                User.builder()
                        .id(UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9813"))
                        .username("testUser")
                        .email("test@mail.ru")
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
                        .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("validUserRequest")
    void createUser_shouldReturn201_whenUserNotExist(RegisterUserRequest request) throws Exception {
        // given
        String requestJson = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
        assertTrue(userRepository.existsByUsername(request.getUsername()));
    }

    @ParameterizedTest
    @MethodSource("invalidUserRequests")
    void createUser_shouldReturn400_whenRequestNotValid(RegisterUserRequest request) throws Exception {
        // given
        String requestJson = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
        assertFalse(userRepository.existsByUsername(any(String.class)));
    }

    @Test
    void updateUserInfo() {
    }

    @Test
    void addSkillToUser() {
    }

    @Test
    void testAddSkillToUser() {
    }

    @Test
    void follow() {
    }

    @Test
    void unfollow() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserFollowers() {
    }

    @Test
    void getUserFollowing() {
    }

    @Test
    void getAllUsers() {
    }

    private static Stream<RegisterUserRequest> validUserRequest() {
        return Stream.of(
                RegisterUserRequest.builder() // Валидный запрос без пароля
                        .username("test")
                        .password("12345")
                        .email("test@mail.ru")
                        .build()
        );
    }

    private static Stream<RegisterUserRequest> invalidUserRequests() {
        return Stream.of(
                new RegisterUserRequest(), // Невалидный запрос без полей
                RegisterUserRequest.builder() // Невалидный запрос без пароля
                        .username("test")
                        .email("test@mail.ru")
                        .build(),
                RegisterUserRequest.builder() // Невалидный запрос без логина
                        .password("12345")
                        .email("test@mail.ru")
                        .build(),
                RegisterUserRequest.builder() // Невалидный запрос без mail
                        .password("12345")
                        .build()
        );
    }
}