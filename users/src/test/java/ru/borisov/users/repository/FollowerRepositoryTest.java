package ru.borisov.users.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.borisov.users.DatabaseTestContainer;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FollowerRepositoryTest extends DatabaseTestContainer {

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private UserRepository userRepository;

    private User to;
    private User from;
    private User other;

    @BeforeEach
    void setUp() {
        to = User.builder()
                .email("to@mail.ru")
                .username("to")
                .passwordHash("password")
                .build();
        from = User.builder()
                .email("from@mail.ru")
                .username("from")
                .passwordHash("password")
                .build();
    }

    @Test
    void findByFromAndTo_shouldReturnFollower_whenUsersFollowEachOther() {

        // given
        userRepository.save(to);
        userRepository.save(from);

        Follower follower = Follower.builder()
                .from(from)
                .to(to)
                .build();
        followerRepository.save(follower);

        // when
        Optional<Follower> followerOptional = followerRepository.findByFromAndTo(from, to);

        // Then
        assertThat(followerOptional).isPresent();
    }

    @Test
    void findByFromAndTo_shouldReturnEmpty_whenUsersNotFollowEachOther() {

        // given
        User other = User.builder()
                .email("other@mail.ru")
                .username("other")
                .passwordHash("password")
                .build();
        userRepository.save(to);
        userRepository.save(from);
        userRepository.save(other);

        Follower follower = Follower.builder()
                .from(from)
                .to(to)
                .build();
        followerRepository.save(follower);

        // when
        Optional<Follower> followerOptional = followerRepository.findByFromAndTo(from, other);

        // Then
        assertThat(followerOptional).isEmpty();
    }
}