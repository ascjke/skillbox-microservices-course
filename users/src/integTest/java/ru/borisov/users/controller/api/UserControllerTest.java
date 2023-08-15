package ru.borisov.users.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.borisov.users.controller.request.AddSkillRequest;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.ExceptionControllerAdvice;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.exception.error.Error;
import ru.borisov.users.exception.error.ErrorResponse;
import ru.borisov.users.model.Gender;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.SkillType;
import ru.borisov.users.model.User;
import ru.borisov.users.service.FollowService;
import ru.borisov.users.service.SkillService;
import ru.borisov.users.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final String USERS_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private ExceptionControllerAdvice exceptionControllerAdvice;

    @MockBean
    private SkillService skillService;

    @MockBean
    private FollowService followService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private User followingUser;

    @BeforeEach
    void setUp() {
        user =
                User.builder()
                        .id(UUID.fromString("5c9bb861-b952-44f1-90d6-9584a86782dc"))
                        .username("testUser")
                        .email("test@mail.ru")
                        .passwordHash("12345")
                        .lastName("Иванов")
                        .firstName("Иван")
                        .middleName("Иванович")
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1990, 6, 23))
                        .city("Якутск")
                        .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                        .bio("nothing to say")
                        .phone("89141002304567")
                        .skills(new HashSet<>())
                        .followers(new HashSet<>())
                        .following(new HashSet<>())
                        .build();
        followingUser =
                User.builder()
                        .id(UUID.fromString("c5b67d20-5b0a-4d75-a76b-08ea06b6f2b6"))
                        .username("testUser2")
                        .email("test2@mail.ru")
                        .passwordHash("12345")
                        .lastName("Иванов2")
                        .firstName("Иван2")
                        .middleName("Иванович2")
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1990, 6, 23))
                        .city("Якутск")
                        .profileImage("https://hsto.org/r/w780/getpro/habr/upload_files/67b/bbe/662/67bbbe662b5b94e1eaa8fc6ec22d2859.jpg")
                        .bio("nothing to say")
                        .phone("89141002304562")
                        .skills(new HashSet<>())
                        .followers(new HashSet<>())
                        .following(new HashSet<>())
                        .build();
    }

    @ParameterizedTest
    @MethodSource("validUserRequest")
    void createUser_shouldReturn201_whenUserNotExist(RegisterUserRequest request) throws Exception {
        // given
        String requestJson = objectMapper.writeValueAsString(request);
        when(userService.registerUser(request)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['data'].id", Matchers.equalTo(user.getId().toString())));
    }

    @ParameterizedTest
    @MethodSource("invalidUserRequests")
    void createUser_shouldReturn400_whenRequestNotValid(RegisterUserRequest request) throws Exception {
        // given
        String requestJson = objectMapper.writeValueAsString(request);
        when(userService.registerUser(request)).thenThrow(CommonException.class);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.REQUEST_VALIDATION_ERROR)
                                .build())
                        .build(), HttpStatus.BAD_REQUEST));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.REQUEST_VALIDATION_ERROR.toString())));
    }

    @Test
    void updateUserInfo_shouldReturn200_whenUserExists() throws Exception {
        // given
        UpdateUserInfoRequest request = UpdateUserInfoRequest.builder()
                .lastName("NewLastName")
                .bio("New bio")
                .build();
        user.setLastName("NewLastName");
        user.setBio("New bio");
        when(userService.updateUserInfo(request, user.getId())).thenReturn(user);
        String requestJson = """
                {
                  "lastName": "NewLastName",
                  "bio": "New bio"
                }
                """;

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put(USERS_URL + "/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].lastName", Matchers.equalTo(user.getLastName())))
                .andExpect(jsonPath("$['data'].bio", Matchers.equalTo(user.getBio())));
    }

    @Test
    void addSkillToUser_shouldReturn200_whenUserExist() throws Exception {
        // given
        AddSkillRequest request = AddSkillRequest.builder()
                .title("java8")
                .skillType(SkillType.HARD_SKILL)
                .build();
        when(skillService.addSkillToUser(request, user.getId())).thenReturn(
                Skill.builder()
                        .title(request.getTitle().toLowerCase())
                        .skillType(request.getSkillType())
                        .build()
        );
        String requestJson = """
                {
                  "title": "java8",
                  "skillType": "HARD_SKILL"
                }
                """;

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL + "/" + user.getId() + "/addSkill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].title", Matchers.equalTo(request.getTitle())))
                .andExpect(jsonPath("$['data'].skillType", Matchers.equalTo(request.getSkillType().toString())));
    }

    @Test
    void addSkillToUser_shouldReturn400_whenInvalidRequest() throws Exception {
        // given
        AddSkillRequest request = new AddSkillRequest();
        when(skillService.addSkillToUser(request, user.getId()))
                .thenThrow(CommonException.class);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.REQUEST_VALIDATION_ERROR)
                                .build())
                        .build(), HttpStatus.BAD_REQUEST));
        String requestJson = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL + "/" + user.getId() + "/addSkill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.REQUEST_VALIDATION_ERROR.toString())));
    }

    @Test
    void addSkillToUser_shouldReturn404_whenUserNotFound() throws Exception {
        // given
        AddSkillRequest request = new AddSkillRequest();
        when(skillService.addSkillToUser(request, user.getId()))
                .thenThrow(CommonException.class);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .build())
                        .build(), HttpStatus.NOT_FOUND));
        String requestJson = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL + "/" + user.getId() + "/addSkill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())));
    }

    @Test
    void removeSkillFromUser_shouldReturn200_whenUserAndSkillExist() throws Exception {

        // given
        UUID skillId = UUID.randomUUID();
        doNothing().when(skillService).removeSkillFromUser(user.getId(), skillId);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete(USERS_URL + "/" + user.getId() + "/removeSkill/" + skillId));

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].message", Matchers.equalTo("Навык удален")));
    }

    @Test
    void removeSkillFromUser_shouldReturn404_whenUserNotFound() throws Exception {

        // given
        UUID skillId = UUID.randomUUID();
        doThrow(CommonException.class).when(skillService).removeSkillFromUser(user.getId(), skillId);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + user.getId() + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete(USERS_URL + "/" + user.getId() + "/removeSkill/" + skillId));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Пользователя с id=" +
                        user.getId() + " не существует!")));
    }

    @Test
    void removeSkillFromUser_shouldReturn404_whenSkillNotFound() throws Exception {
        // given
        UUID nonExistingSkillId = UUID.randomUUID();
        doThrow(CommonException.class).when(skillService).removeSkillFromUser(user.getId(), nonExistingSkillId);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.SKILL_NOT_FOUND)
                                .message("Навыка с id=" + nonExistingSkillId + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete(USERS_URL + "/" + user.getId() + "/removeSkill/" + nonExistingSkillId));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.SKILL_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Навыка с id=" +
                        nonExistingSkillId + " не существует!")));
    }

    @Test
    void follow_shouldReturn200_whenUsersExistAndNotFollowing() throws Exception {
        // given
        doNothing().when(followService).follow(user.getId(), followingUser.getId());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put(USERS_URL + "/" + user.getId() + "/follow/" + followingUser.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].message", Matchers.equalTo("Вы успешно подписались")));
    }

    @Test
    void follow_shouldReturn404_whenUserNotExist() throws Exception {
        // given
        UUID followingUserId = UUID.randomUUID();
        doThrow(CommonException.class).when(followService).follow(user.getId(), followingUserId);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + followingUserId + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put(USERS_URL + "/" + user.getId() + "/follow/" + followingUserId));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Пользователя с id=" +
                        followingUserId + " не существует!")));
    }

    @Test
    void follow_shouldReturn409_whenAlreadyFollowing() throws Exception {
        // given
        doThrow(CommonException.class).when(followService).follow(user.getId(), followingUser.getId());
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.CONFLICT)
                                .message("Вы уже подписаны на этого пользователя!")
                                .build())
                        .build(), HttpStatus.CONFLICT));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put(USERS_URL + "/" + user.getId() + "/follow/" + followingUser.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.CONFLICT.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Вы уже подписаны на этого пользователя!")));
    }

    @Test
    void unfollow_shouldReturn200_whenUsersExistAndFollowing() throws Exception {
        // given
        doNothing().when(followService).unfollow(user.getId(), followingUser.getId());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete(USERS_URL + "/" + user.getId() + "/unfollow/" + followingUser.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].message", Matchers.equalTo("Вы отписались от пользователя")));
    }


    @Test
    void unfollow_shouldReturn404_whenUserNotExist() throws Exception {
        // given
        UUID followingUserId = UUID.randomUUID();
        doThrow(CommonException.class).when(followService).unfollow(user.getId(), followingUserId);
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + followingUserId + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete(USERS_URL + "/" + user.getId() + "/unfollow/" + followingUserId));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Пользователя с id=" +
                        followingUserId + " не существует!")));
    }


    @Test
    void getUserById_shouldReturn200_whenUserExists() throws Exception {
        // given
        when(userService.getUserById(user.getId())).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_URL + "/" + user.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$['data'].username", Matchers.equalTo(user.getUsername())));
    }

    @Test
    void getUserById_shouldReturn404_whenUserNotExist() throws Exception {
        // given
        doThrow(CommonException.class).when(userService).getUserById(user.getId());
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + user.getId() + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_URL + "/" + user.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Пользователя с id=" +
                        user.getId() + " не существует!")));
    }

    @Test
    void getUserFollowers_shouldReturn200AndEmptyData_whenThereAreNoFollowers() throws Exception {
        // given
        when(userService.getUserFollowers(user.getId())).thenReturn(new HashSet<>());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_URL + "/" + user.getId() + "/followers"));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "data": [
                          ]
                        }
                        """));
    }

    @Test
    void getUserFollowers_shouldReturn404_whenUserNotExist() throws Exception {
        // given
        doThrow(CommonException.class).when(userService).getUserFollowers(user.getId());
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + user.getId() + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_URL + "/" + user.getId() + "/followers"));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$['error'].code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$['error'].message", Matchers.equalTo("Пользователя с id=" +
                        user.getId() + " не существует!")));
    }

    @Test
    void getUserFollowing_shouldReturn200AndFollowing_whenUserExists() throws Exception {
        // given
        User following1 = User.builder()
                .id(UUID.randomUUID())
                .username("following1")
                .passwordHash("12345")
                .email("following1@example.com")
                .following(new HashSet<>())
                .followers(new HashSet<>())
                .build();
        User following2 = User.builder()
                .id(UUID.randomUUID())
                .username("following2")
                .passwordHash("12345")
                .email("following2@example.com")
                .following(new HashSet<>())
                .followers(new HashSet<>())
                .build();
        Set<User> following = Set.of(following1, following2);
        when(userService.getUserFollowing(user.getId())).thenReturn(following);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_URL + "/" + user.getId() + "/following"));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.length()", Matchers.equalTo(following.size())))
                .andExpect(jsonPath("$.data[*].username", Matchers.containsInAnyOrder(
                        following1.getUsername(), following2.getUsername())));
    }

    @Test
    void getAllUsers_shouldReturn200AndEmptyList_whenNoUsersExist() throws Exception {
        List<User> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(USERS_URL));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.length()", Matchers.equalTo(users.size())));
    }

    @Test
    void getAllUsers_shouldReturn200AndUserList_whenUsersExist() throws Exception {
        // given
        List<User> users = List.of(user, followingUser);
        when(userService.getAllUsers()).thenReturn(users);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(USERS_URL));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.length()", Matchers.equalTo(users.size())))
                .andExpect(jsonPath("$.data.[0].username", Matchers.equalTo(user.getUsername())))
                .andExpect(jsonPath("$.data.[1].username", Matchers.equalTo(followingUser.getUsername())));
    }

    @Test
    void removeUserById_shouldReturn200_whenUserExists() throws Exception {
        // given
        when(userService.removeUserById(user.getId())).thenReturn(ApiResponse.builder()
                .success(true)
                .message("Пользователь " + user.getUsername() + " успешно удален!")
                .build());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + user.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.success", Matchers.equalTo(true)))
                .andExpect(jsonPath("$.data.message", Matchers.equalTo("Пользователь " + user.getUsername() + " успешно удален!")));
    }

    @Test
    void removeUserById_shouldReturn404_whenUserDoesNotExist() throws Exception {
        // given
        doThrow(CommonException.class).when(userService).removeUserById(user.getId());
        when(exceptionControllerAdvice.handleCommonException(new CommonException())).thenReturn(
                new ResponseEntity<>(ErrorResponse.builder()
                        .error(Error.builder()
                                .code(Code.USER_NOT_FOUND)
                                .message("Пользователя с id=" + user.getId() + " не существует!")
                                .build())
                        .build(), HttpStatus.NOT_FOUND));


        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + user.getId()));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.equalTo(Code.USER_NOT_FOUND.toString())))
                .andExpect(jsonPath("$.error.message", Matchers.equalTo("Пользователя с id=" + user.getId() + " не существует!")));
    }

    private static Stream<RegisterUserRequest> validUserRequest() {
        return Stream.of(
                RegisterUserRequest.builder() // Валидный запрос
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