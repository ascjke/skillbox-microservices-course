package ru.borisov.users;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.borisov.users.repository.FollowerRepository;
import ru.borisov.users.repository.SkillRepository;
import ru.borisov.users.repository.UserRepository;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

//@DataJpaTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
public class DatabaseTestContainer {

    private static final String DATABASE_NAME = "users";
    private static int containerPort = 5432;
    private static int localPort = 5440;

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName(DATABASE_NAME)
            .withUsername("postgres")
            .withPassword("password")
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ));

    @Autowired
    protected SkillRepository skillRepository;

    @Autowired
    protected FollowerRepository followerRepository;

    @Autowired
    protected UserRepository userRepository;

    @DynamicPropertySource
    static void datasourceProps(final DynamicPropertyRegistry registry) {
        postgreSQLContainer.start();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
