package ru.borisov.users.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.borisov.users.model.Male;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CreateUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String passwordHash;

    private String lastName;
    private String firstName;
    private String middleName;
    private Male male;
    private LocalDate birthDate;
    private String city;
    private String profileImage;
    private String bio;
    private List<String> hardSkills;
    private String phone;
}
