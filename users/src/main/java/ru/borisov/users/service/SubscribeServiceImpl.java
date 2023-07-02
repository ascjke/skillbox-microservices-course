package ru.borisov.users.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.Following;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.FollowerRepository;
import ru.borisov.users.repository.FollowingRepository;
import ru.borisov.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeServiceImpl implements SubscribeService {

    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void requestSubscription(UUID currentUserId, UUID followingUserId) {

        log.info("Пользователь с id={} отправил запрос на подписку пользователю {}", currentUserId::toString, followingUserId::toString);
        User currentUser = userService.getUserById(currentUserId);
        User followingUser = userService.getUserById(followingUserId);
        Optional<Following> optionalFollowing = followingRepository.findByUser(followingUser);
        if (optionalFollowing.isPresent()) {
            throw new CommonException(Code.CONFLICT,
                    "Вы уже отправляли запрос на подписку на данного пользователя!",
                    HttpStatus.CONFLICT);
        }

        Following following = Following.builder()
                .user(followingUser)
                .build();

        Follower follower = Follower.builder()
                .user(currentUser)
                .build();

        currentUser.getFollowings().add(following);
        followingUser.getFollowers().add(follower);

        userRepository.save(currentUser);
        userRepository.save(followingUser);
        log.info("Пользователь {} отправил запрос на подписку пользователю {}", currentUser::getUsername, followingUser::getUsername);
    }

    @Override
    @Transactional
    public List<Follower> getSubscriptionRequests(UUID id) {

        User user = userService.getUserById(id);
        return user.getFollowers().stream()
                .filter(follower -> !follower.isConfirmed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void confirmSubscription(UUID userId, UUID followerUserId) {

        User currentUser = userService.getUserById(userId);
        User followerUser = userService.getUserById(followerUserId);

        Follower follower = findFollowerByUser(currentUser, followerUser);
        if (follower != null) {
            follower.setConfirmed(true);
        }

        Following following = findFollowingByUser(followerUser, currentUser);
        if (following == null || following.isConfirmed()) {
            throw new CommonException(Code.USER_NOT_FOUND,
                    "Запрос на подписку не найден или уже подтвержден!",
                    HttpStatus.NOT_FOUND);
        }
        following.setConfirmed(true);

        userRepository.save(currentUser);
        userRepository.save(followerUser);
        log.info("Пользователь {} подписался на пользователя {}", followerUser::getUsername, currentUser::getUsername);
    }

    @Override
    @Transactional
    public void unsubscribeUser(UUID userId, UUID followingUserId) {
        User currentUser = userService.getUserById(userId);
        User followingUserToBeUnsubscribed = userService.getUserById(followingUserId);

        Following following = findFollowingByUser(currentUser, followingUserToBeUnsubscribed);
        if (following != null) {
            currentUser.getFollowings().remove(following);
        }

        Follower follower = findFollowerByUser(followingUserToBeUnsubscribed, currentUser);
        if (follower != null) {
            followingUserToBeUnsubscribed.getFollowers().remove(follower);
        }

        userRepository.save(currentUser);
        userRepository.save(followingUserToBeUnsubscribed);
        log.info("Пользователь {} отписался от пользователя {}", currentUser::getUsername, followingUserToBeUnsubscribed::getUsername);
    }


    private Following findFollowingByUser(User currentUser, User followingUser) {
        return currentUser.getFollowings()
                .stream()
                .filter(f -> f.getUser().equals(followingUser))
                .findFirst()
                .orElse(null);
    }

    private Follower findFollowerByUser(User currentUser, User followerUser) {
        return currentUser.getFollowers()
                .stream()
                .filter(f -> f.getUser().equals(followerUser))
                .findFirst()
                .orElse(null);
    }


}
