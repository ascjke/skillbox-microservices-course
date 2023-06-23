package ru.borisov.users.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.controller.request.EditUserRequest;
import ru.borisov.users.controller.response.Response;
import ru.borisov.users.controller.response.SuccessResponse;
import ru.borisov.users.service.UserServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody CreateUserRequest request) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.createUser(request))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Response> editUser(@RequestBody EditUserRequest request,
                                             @PathVariable UUID uuid) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.editUser(request, uuid))
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Response> getUserById(@PathVariable UUID uuid) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getUserById(uuid))
                .build(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Response> getAllUsers() {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getAllUsers())
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Response> removeUser(@PathVariable UUID uuid) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.removeUser(uuid))
                .build(), HttpStatus.OK);
    }
}
