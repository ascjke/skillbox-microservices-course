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
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.ExceptionControllerAdvice;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.exception.error.Error;
import ru.borisov.users.exception.error.ErrorResponse;
import ru.borisov.users.model.Gender;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.SkillType;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.UserRepository;
import ru.borisov.users.service.FollowService;
import ru.borisov.users.service.SkillService;
import ru.borisov.users.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
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

    @MockBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private User followingUser;
    private Skill skill;

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
        skill = Skill.builder()
                .title("java8")
                .skillType(SkillType.HARD_SKILL)
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

//    @Test
//    void removeSkillFromUser_shouldReturn200_whenUserAndSkillExist() throws Exception {
//
//        // given
//        user = userRepository.save(user);
//        skill = skillRepository.save(skill);
//        user.getSkills().add(skill);
//        userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + user.getId() + "/removeSkill/" + skill.getId()));
//
//        // then
//        resultActions
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                           "data":
//                           {
//                                "success": true,
//                                "message": "Навык удален"
//                           }
//                        }
//                        """));
//
//        // Check that the skill is removed from the user
//        user = userRepository.findById(user.getId()).orElse(null);
//        Skill finalSkill = skill;
//        assertFalse(user.getSkills().stream().anyMatch(s -> s.getTitle().equals(finalSkill.getTitle())));
//    }

    //    @Test
//    @Transactional
//    void removeSkillFromUser_shouldReturn404_whenUserNotFound() throws Exception {
//
//        // given
//        UUID nonExistingUserId = UUID.randomUUID();
//        skill = skillRepository.save(skill);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + nonExistingUserId + "/removeSkill/" + skill.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(nonExistingUserId)));
//    }
//
//    @Test
//    @Transactional
//    void removeSkillFromUser_shouldReturn404_whenSkillNotFound() throws Exception {
//        // given
//        user = userRepository.save(user);
//        UUID nonExistingSkillId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + user.getId() + "/removeSkill/" + nonExistingSkillId));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "SKILL_NOT_FOUND",
//                            "message": "Навыка с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(nonExistingSkillId)));
//    }
//
//    @Test
//    @Transactional
//    void follow_shouldReturn200_whenUsersExistAndNotFollowing() throws Exception {
//        // given
//        user = userRepository.save(user);
//        followingUser = userRepository.save(followingUser);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .put(USERS_URL + "/" + user.getId() + "/follow/" + followingUser.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": {
//                            "success": true,
//                            "message": "Вы успешно подписались"
//                          }
//                        }
//                        """));
//        Subscription subscription = followerRepository.findByFromAndTo(user, followingUser).orElse(null);
//        assertNotNull(subscription);
//        assertEquals(subscription.getFrom(), user);
//        assertEquals(subscription.getTo(), followingUser);
//    }
//
//    @Test
//    void follow_shouldReturn404_whenUserNotExist() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//        UUID followingUserId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .put(USERS_URL + "/" + userId + "/follow/" + followingUserId));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(userId)));
//    }
//
//    @Test
//    void follow_shouldReturn409_whenAlreadyFollowing() throws Exception {
//        // given
//        user = userRepository.save(user);
//        followingUser = userRepository.save(followingUser);
//        Subscription subscription = Subscription.builder()
//                .from(user)
//                .to(followingUser)
//                .build();
//        followerRepository.save(subscription);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .put(USERS_URL + "/" + user.getId() + "/follow/" + followingUser.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isConflict())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "CONFLICT",
//                            "message": "Вы уже подписаны на этого пользователя!"
//                          }
//                        }
//                        """));
//    }
//
//    @Test
//    void unfollow_shouldReturn200AndDeleteFollowerFromDb_whenUsersExistAndFollowing() throws Exception {
//        // given
//        user = userRepository.save(user);
//        followingUser = userRepository.save(followingUser);
//        Subscription subscription = Subscription.builder()
//                .from(user)
//                .to(followingUser)
//                .build();
//        followerRepository.save(subscription);
//
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + user.getId() + "/unfollow/" + followingUser.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": {
//                            "success": true,
//                            "message": "Вы отписались от пользователя"
//                          }
//                        }
//                        """));
//        subscription = followerRepository.findByFromAndTo(user, followingUser).orElse(null);
//        assertNull(subscription);
//    }
//
//    @Test
//    void unfollow_shouldReturn404_whenUserNotExist() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//        UUID followingUserId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + userId + "/unfollow/" + followingUserId));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(userId)));
//    }
//
//
//    @Test
//    void unfollow_shouldReturn409_whenNotFollowing() throws Exception {
//        // given
//        user = userRepository.save(user);
//        followingUser = userRepository.save(followingUser);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .delete(USERS_URL + "/" + user.getId() + "/unfollow/" + followingUser.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isConflict())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "CONFLICT",
//                            "message": "Вы не подписаны на этого пользователя!"
//                          }
//                        }
//                        """));
//    }
//
//    @Test
//    void getUserById_shouldReturn200_whenUserExists() throws Exception {
//        // given
//        user = userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + user.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": {
//                            "id": "%s",
//                            "username": "testUser",
//                            "email": "test@mail.ru"
//                          }
//                        }
//                        """.formatted(user.getId())));
//    }
//
//    @Test
//    void getUserById_shouldReturn404_whenUserNotExist() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + userId));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(userId)));
//    }
//
//    @Test
//    @Transactional
//    void getUserFollowers_shouldReturn200AndFollowers_whenUserExists() throws Exception {
//
//        // given
//        user = userRepository.save(user);
//        User follower1 = User.builder()
//                .id(UUID.randomUUID())
//                .username("follower1")
//                .passwordHash("12345")
//                .email("follower1@example.com")
//                .following(new HashSet<>())
//                .followers(new HashSet<>())
//                .build();
//        User follower2 = User.builder()
//                .id(UUID.randomUUID())
//                .username("follower2")
//                .passwordHash("12345")
//                .email("follower2@example.com")
//                .following(new HashSet<>())
//                .followers(new HashSet<>())
//                .build();
//
//        // Save the followers to the repository
//        follower1 = userRepository.save(follower1);
//        follower2 = userRepository.save(follower2);
//
//        Subscription _subscription1 = Subscription.builder()
//                .from(follower1)
//                .to(user)
//                .build();
//        Subscription _subscription2 = Subscription.builder()
//                .from(follower2)
//                .to(user)
//                .build();
//        _subscription1 = followerRepository.save(_subscription1);
//        _subscription2 = followerRepository.save(_subscription2);
//
//
//        // Add the followers to the user's followers list
//        user.getFollowers().add(_subscription1);
//        user.getFollowers().add(_subscription2);
//
//        // Save the user to the repository
//        user = userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + user.getId() + "/followers"));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": [
//                            {
//                              "id": "%s",
//                              "username": "follower1",
//                              "email": "follower1@example.com"
//                            },
//                            {
//                              "id": "%s",
//                              "username": "follower2",
//                              "email": "follower2@example.com"
//                            }
//                          ]
//                        }
//                        """.formatted(follower1.getId(), follower2.getId())));
//    }
//
//    @Test
//    @Transactional
//    void getUserFollowers_shouldReturn200AndEmptyData_whenThereAreNoFollowers() throws Exception {
//
//        // given
//        user = userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + user.getId() + "/followers"));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": [
//                          ]
//                        }
//                        """));
//    }
//
//    @Test
//    void getUserFollowers_shouldReturn404_whenUserNotExist() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + userId + "/followers"));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(userId)));
//    }
//
//    @Test
//    @Transactional
//    void getUserFollowing_shouldReturn200AndFollowing_whenUserExists() throws Exception {
//
//        // given
//        user = userRepository.save(user);
//        User following1 = User.builder()
//                .id(UUID.randomUUID())
//                .username("following1")
//                .passwordHash("12345")
//                .email("following1@example.com")
//                .following(new HashSet<>())
//                .followers(new HashSet<>())
//                .build();
//        User following2 = User.builder()
//                .id(UUID.randomUUID())
//                .username("following2")
//                .passwordHash("12345")
//                .email("following2@example.com")
//                .following(new HashSet<>())
//                .followers(new HashSet<>())
//                .build();
//
//        // Save the followers to the repository
//        following1 = userRepository.save(following1);
//        following2 = userRepository.save(following2);
//
//        Subscription _following1 = Subscription.builder()
//                .from(user)
//                .to(following1)
//                .build();
//        Subscription _following2 = Subscription.builder()
//                .from(user)
//                .to(following2)
//                .build();
//        _following1 = followerRepository.save(_following1);
//        _following2 = followerRepository.save(_following2);
//
//
//        // Add the followers to the user's followers list
//        user.getFollowing().add(_following1);
//        user.getFollowing().add(_following2);
//
//        // Save the user to the repository
//        user = userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + user.getId() + "/following"));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": [
//                            {
//                              "id": "%s",
//                              "username": "following1",
//                              "email": "following1@example.com"
//                            },
//                            {
//                              "id": "%s",
//                              "username": "following2",
//                              "email": "following2@example.com"
//                            }
//                          ]
//                        }
//                        """.formatted(following1.getId(), following2.getId())));
//    }
//
//    @Test
//    void getUserFollowing_shouldReturn404_whenUserNotExist() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//                .get(USERS_URL + "/" + userId + "/following"));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(userId)));
//    }
//
//    @Test
//    void getAllUsers_shouldReturn200AndEmptyList_whenNoUsersExist() throws Exception {
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(USERS_URL));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": []
//                        }
//                        """));
//    }
//
//    @Test
//    void getAllUsers_shouldReturn200AndUserList_whenUsersExist() throws Exception {
//        // given
//        user = userRepository.save(user);
//        followingUser = userRepository.save(followingUser);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(USERS_URL));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": [
//                            {
//                              "id": "%s",
//                              "username": "%s",
//                              "email": "%s"
//                            },
//                            {
//                              "id": "%s",
//                              "username": "%s",
//                              "email": "%s"
//                            }
//                          ]
//                        }
//                        """.formatted(user.getId(), user.getUsername(), user.getEmail(),
//                        followingUser.getId(), followingUser.getUsername(), followingUser.getEmail())));
//    }
//
//    @Test
//    void removeUserById_shouldReturn200_whenUserExists() throws Exception {
//        // given
//        user = userRepository.save(user);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + user.getId()));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "data": {
//                            "success": true,
//                            "message": "Пользователь %s успешно удален!"
//                          }
//                        }
//                        """.formatted(user.getUsername())));
//    }
//
//    @Test
//    void removeUserById_shouldReturn404_whenUserDoesNotExist() throws Exception {
//        // given
//        UUID nonExistentUserId = UUID.randomUUID();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + nonExistentUserId));
//
//        // then
//        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().json("""
//                        {
//                          "error": {
//                            "code": "USER_NOT_FOUND",
//                            "message": "Пользователя с id=%s не существует!"
//                          }
//                        }
//                        """.formatted(nonExistentUserId)));
//    }
//
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