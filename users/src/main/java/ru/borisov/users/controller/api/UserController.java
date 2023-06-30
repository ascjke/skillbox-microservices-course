package ru.borisov.users.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borisov.users.controller.request.RegisterUserRequest;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;
import ru.borisov.users.controller.response.ApiResponse;
import ru.borisov.users.controller.response.Response;
import ru.borisov.users.controller.response.SuccessResponse;
import ru.borisov.users.model.Follower;
import ru.borisov.users.model.Following;
import ru.borisov.users.model.Skill;
import ru.borisov.users.model.User;
import ru.borisov.users.service.SubscribeService;
import ru.borisov.users.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SubscribeService subscribeService;

    @PostMapping
    @Operation(summary = "Регистрация пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "CREATED",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "BAD_REQUEST")
            })
    public ResponseEntity<Response> registerUser(@RequestBody RegisterUserRequest request) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.registerUser(request))
                .build(), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Заполнить данные пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> updateUserInfo(
            @RequestBody UpdateUserInfoRequest request,
            @PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.updateUserInfo(request, id))
                .build(), HttpStatus.OK);
    }

    @PostMapping("/{id}/addSkill")
    @Operation(summary = "Добавить skill пользователю",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Skill.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "BAD_REQUEST")
            })
    public ResponseEntity<Response> addSkillToUser(
            @RequestBody UpdateUserInfoRequest request,
            @PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.addSkillToUser(request, id))
                .build(), HttpStatus.OK);
    }


    @PutMapping("/{id}/followRequest/{followingUserId}")
    @Operation(summary = "Отправить запрос на подписку",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "CONFLICT")
            })
    public ResponseEntity<Response> requestSubscription(
            @PathVariable UUID id,
            @PathVariable UUID followingUserId) {

        subscribeService.requestSubscription(id, followingUserId);

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(ApiResponse.builder()
                        .success(true)
                        .message("Вы отправили запрос на подписку")
                        .build())
                .build(), HttpStatus.OK);
    }


    @PutMapping("/{id}/confirmFollower/{followerUserId}")
    @Operation(summary = "Разрешить подписку",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> confirmSubscription(
            @PathVariable UUID id,
            @PathVariable UUID followerUserId) {

        subscribeService.confirmSubscription(id, followerUserId);

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(ApiResponse.builder()
                        .success(true)
                        .message("Вы разрешили подписку пользователю")
                        .build())
                .build(), HttpStatus.OK);
    }


    @DeleteMapping("/{id}/unSubscribe/{followingUserId}")
    @Operation(summary = "Отписаться от пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> unsubscribeUser(
            @PathVariable UUID id,
            @PathVariable UUID followingUserId) {

        subscribeService.unsubscribeUser(id, followingUserId);

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(ApiResponse.builder()
                        .success(true)
                        .message("Вы отписалсиь от пользователя")
                        .build())
                .build(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> getUserById(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getUserById(id))
                .build(), HttpStatus.OK);
    }


    @GetMapping("/{id}/followers")
    @Operation(summary = "Подписчики пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Follower[].class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> getUserFollowers(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getUserFollowers(id))
                .build(), HttpStatus.OK);
    }


    @GetMapping("/{id}/followings")
    @Operation(summary = "Подписки пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = Following[].class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "NOT_FOUND")
            })
    public ResponseEntity<Response> getUserFollowings(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getUserFollowings(id))
                .build(), HttpStatus.OK);
    }


    @GetMapping
    @Operation(summary = "Список всех пользователей",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = User[].class)))
            })
    public ResponseEntity<Response> getAllUsers() {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getAllUsers())
                .build(), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить профиль пользователя",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    public ResponseEntity<Response> removeUserById(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.removeUserById(id))
                .build(), HttpStatus.OK);
    }
}
