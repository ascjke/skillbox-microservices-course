package ru.borisov.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.model.Subscription;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.FollowerRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FollowServiceImplTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FollowServiceImpl followService;

    private User from;
    private User to;
    private UUID userId;
    private UUID followingUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9813");
        followingUserId = UUID.fromString("4d5d6017-980a-45e1-be03-9df962af9814");

        from = User.builder()
                .id(userId)
                .username("ascjke")
                .email("ascjke@mail.ru")
                .passwordHash("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/8oY4FmU7FkjqaO")
                .followers(new HashSet<>())
                .following(new HashSet<>())
                .build();

        to = User.builder()
                .id(followingUserId)
                .username("test")
                .email("test@mail.ru")
                .passwordHash("$2a$12$Vj44jG3s5x6x01XqmCN.B.6sxldIRFSsXzX1TA/laskdljkashd8")
                .followers(new HashSet<>())
                .following(new HashSet<>())
                .build();
    }

    @Test
    void follow_shouldSaveFollower_whenUsersAreNotFollowing() {

        // given
        when(userService.getUserById(userId)).thenReturn(from);
        when(userService.getUserById(followingUserId)).thenReturn(to);
        when(followerRepository.findByFromAndTo(from, to)).thenReturn(Optional.empty());

        // when
        followService.follow(userId, followingUserId);

        // then
        verify(followerRepository, Mockito.times(1)).save(any(Subscription.class));
    }

    @Test
    void follow_shouldThrowException_whenUsersAreAlreadyFollowing() {

        // given
        when(userService.getUserById(userId)).thenReturn(from);
        when(userService.getUserById(followingUserId)).thenReturn(to);
        when(followerRepository.findByFromAndTo(from, to)).thenReturn(Optional.of(new Subscription()));

        // then
        assertThrows(CommonException.class, () -> {
            // when
            followService.follow(userId, followingUserId);
        });
        verify(followerRepository, never()).save(any(Subscription.class));
    }

    @Test
    void unfollow_shouldDeleteFollower_whenFollowerExists() {

        // given
        when(userService.getUserById(userId)).thenReturn(from);
        when(userService.getUserById(followingUserId)).thenReturn(to);
        when(followerRepository.findByFromAndTo(from, to)).thenReturn(Optional.of(new Subscription()));

        // when
        followService.unfollow(userId, followingUserId);

        // then
        verify(followerRepository, times(1)).delete(any(Subscription.class));
    }

    @Test
    void unfollow_shouldThrowException_whenFollowerDoesNotExist() {
        // given
        when(userService.getUserById(userId)).thenReturn(from);
        when(userService.getUserById(followingUserId)).thenReturn(to);
        when(followerRepository.findByFromAndTo(from, to)).thenReturn(Optional.empty());

        // then
        assertThrows(CommonException.class, () -> {
            // when
            followService.unfollow(userId, followingUserId);
        });
        verify(followerRepository, never()).delete(any(Subscription.class));
    }
}