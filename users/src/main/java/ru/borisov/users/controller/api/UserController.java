package ru.borisov.users.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.borisov.users.controller.request.CreateUserRequest;
import ru.borisov.users.controller.request.EditUserRequest;
import ru.borisov.users.controller.response.Response;
import ru.borisov.users.controller.response.SuccessResponse;
import ru.borisov.users.service.UserServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody CreateUserRequest request) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.createUser(request))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<Response> editUser(@RequestBody EditUserRequest request,
                                             @PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.editUser(request, id))
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<Response> getUserById(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getUserById(id))
                .build(), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('client_admin', 'client_user')")
    public ResponseEntity<Response> getAllUsers() {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.getAllUsers())
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<Response> removeUserById(@PathVariable UUID id) {

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(userService.removeUserById(id))
                .build(), HttpStatus.OK);
    }
}
