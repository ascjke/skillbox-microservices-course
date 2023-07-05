package ru.borisov.users.controller.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.borisov.users.DatabaseTestContainer;
import ru.borisov.users.model.Male;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class UserControllerTest extends DatabaseTestContainer {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private User user;
    private static final String USERS_URL = "/api/users";


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

    @Test
    void createUser() throws Exception {

        //given
        String request = """
                {
                  "username": "testUser",
                  "email": "test@mail.ru",
                  "password": "12345"
                }
                """;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
        assertTrue(userRepository.existsByUsername("testUser"));
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