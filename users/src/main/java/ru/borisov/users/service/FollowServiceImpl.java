package ru.borisov.users.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.User;
import ru.borisov.users.repository.FollowerRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class FollowServiceImpl implements FollowService {

    private final FollowerRepository followerRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void follow(UUID currentUserId, UUID followingUserId) {

        log.info("Пользователь с id={} отправил запрос на подписку пользователю {}", currentUserId::toString, followingUserId::toString);
        User from = userService.getUserById(currentUserId);
        User to = userService.getUserById(followingUserId);

        if (followerRepository.findByFromAndTo(from, to).isPresent()) {
            throw new CommonException(Code.CONFLICT,
                    "Вы уже подписаны на этого пользователя!",
                    HttpStatus.CONFLICT);
        }

        Follower follower = Follower.builder()
                .from(from)
                .to(to)
                .build();

        followerRepository.save(follower);


        log.info("Пользователь {} подписался на пользователя {}", from::getUsername, to::getUsername);
    }


    @Override
    @Transactional
    public void unfollow(UUID userId, UUID followingUserId) {
        User from = userService.getUserById(userId);
        User to = userService.getUserById(followingUserId);

        Optional<Follower> followerOptional = followerRepository.findByFromAndTo(from, to);

        if (followerOptional.isEmpty()) {
            throw new CommonException(Code.CONFLICT,
                    "Вы не подписаны на этого пользователя!",
                    HttpStatus.CONFLICT);
        }

        followerRepository.delete(followerOptional.get());
        log.info("Пользователь {} отписался от пользователя {}", from.getUsername(), to.getUsername());
    }
}
